/// <reference path="../node_modules/typescript/lib/typescriptServices.d.ts"/>
/// <reference path="../node_modules/typescript/lib/tsserverlibrary.d.ts"/>

if (typeof ts == "undefined") {
  (global as any).ts = require("typescript/lib/tsserverlibrary");
}

declare function print(...arg: any[]): void;
declare function println(arg: String): void;

declare class FileResolverV8 implements FileResolver {
  resolve(fileName: string): string;
}

if (typeof console == "undefined") {
  (global as any).console = {
    log: (...args: any[]) => {
      println(args.map(it => String(it)).join(", "));
    }
  }
}


interface FileResolver {
  resolve(fileName: string): string;
}

function resolveType(astFactory: TypescriptAstFactory, type: ts.TypeNode | undefined) : TypeDeclaration {
  if (type == undefined) {
    return astFactory.createTypeDeclaration("Unit")
  } else {
    if (ts.isArrayTypeNode(type)) {
      let arrayType = type as ts.ArrayTypeNode;
      return astFactory.createTypeDeclaration("@@ArraySugar", [
        resolveType(astFactory, arrayType.elementType)
      ] as Array<TypeDeclaration>)
    } else {
      if (ts.isUnionTypeNode(type)) {
        let unionTypeNode = type as ts.UnionTypeNode;
        let params = unionTypeNode.types
                      .map(argumentType => resolveType(astFactory, argumentType)) as Array<TypeDeclaration>;

        return astFactory.createUnionType(params)
      } else if (type.kind == ts.SyntaxKind.TypeReference) {
        let typeReferenceNode = type as ts.TypeReferenceNode;
        if (typeof typeReferenceNode.typeArguments != "undefined") {
            let params = typeReferenceNode.typeArguments
              .map(argumentType => resolveType(astFactory, argumentType)) as Array<TypeDeclaration>;

            return astFactory.createTypeDeclaration(typeReferenceNode.typeName.getText(), params)
        } else {
            return astFactory.createTypeDeclaration(typeReferenceNode.typeName.getText())
        }
      } else if (type.kind == ts.SyntaxKind.ParenthesizedType) {
        let parenthesizedTypeNode = type as ts.ParenthesizedTypeNode;
        return resolveType(astFactory, parenthesizedTypeNode.type);
      } else if (type.kind == ts.SyntaxKind.NullKeyword) {
        return astFactory.createTypeDeclaration("null")
      } else if (type.kind == ts.SyntaxKind.UndefinedKeyword) {
        return astFactory.createTypeDeclaration("undefined")
      } else if (type.kind == ts.SyntaxKind.StringKeyword) {
        return astFactory.createTypeDeclaration("string")
      } else if (type.kind == ts.SyntaxKind.BooleanKeyword) {
        return astFactory.createTypeDeclaration("boolean")
      } else if (type.kind == ts.SyntaxKind.NumberKeyword) {
        return astFactory.createTypeDeclaration("number")
      } else if (type.kind == ts.SyntaxKind.AnyKeyword) {
        return astFactory.createTypeDeclaration("any")
      } else {
        return astFactory.createTypeDeclaration("__UNKNOWN__")
      }
    }
  }
}


function createParamDeclaration(astFactory: TypescriptAstFactory, param: ts.ParameterDeclaration) : ParameterDeclaration {

  let initializer = null;
  if (param.initializer != null) {
    initializer = astFactory.createExpression(
        astFactory.createTypeDeclaration("@@DEFINED_EXTERNALLY"),
        param.initializer.getText()
    )
  } else if (param.questionToken != null) {
      initializer = astFactory.createExpression(
          astFactory.createTypeDeclaration("@@DEFINED_EXTERNALLY"),
          "null"
      )
  }

  let paramType = resolveType(astFactory, param.type);

  return astFactory.createParameterDeclaration(
      param.name.getText(),
      param.questionToken ? astFactory.createNullableType(paramType) : paramType,
      initializer
  )
}

function main(nativeAstFactory: AstFactory, fileResolver: FileResolver, fileName: string)  {

  let astFactory: TypescriptAstFactory = nativeAstFactory == null ?
              new TypescriptAstFactory(new AstFactoryV8()) : new TypescriptAstFactory(nativeAstFactory);


  if (fileResolver == null) {
    fileResolver = new FileResolverV8();
  }


  let documentRegistry = ts.createDocumentRegistry();

  let host= new DukatLanguageServiceHost(fileResolver);
  host.register(fileName);

  let languageService = ts.createLanguageService(host, documentRegistry);

  var program = languageService.getProgram();

  var declarations: Declaration[] = [];

  if (program != null) {
    var sourceFile = program.getSourceFile(fileName);

    if (sourceFile != null) {

      for (let statement of sourceFile.statements) {
        if (ts.isVariableStatement(statement)) {
          const variableStatment = statement as ts.VariableStatement;

          const hasModifiers = variableStatment.modifiers != undefined;

          if (hasModifiers) {
            for (let declaration of variableStatment.declarationList.declarations) {
              declarations.push(astFactory.declareVariable(
                  declaration.name.getText(),
                  astFactory.resolveType(declaration.type)
              ));
            }
          }
        } else if (ts.isClassDeclaration(statement)) {
          const classDeclaration = statement as ts.ClassDeclaration;

          if (classDeclaration.name != undefined) {

            let members: Array<MemberDeclaration> = [];

            if (classDeclaration.members) {

              for (let memberDeclaration of classDeclaration.members) {
                if (memberDeclaration.kind == ts.SyntaxKind.IndexSignature) {
                  let indexSignatureDeclaration = memberDeclaration as ts.IndexSignatureDeclaration;

                  let typeParameterDeclarations: Array<TypeParameter> = astFactory.convertTypeParams(indexSignatureDeclaration.typeParameters);
                  let parameterDeclarations = indexSignatureDeclaration.parameters
                      .map(
                          param => astFactory.createParamDeclaration(param)
                      );

                  members.push(astFactory.createMethodDeclaration(
                      "get", parameterDeclarations, astFactory.createNullableType(astFactory.resolveType(indexSignatureDeclaration.type)), typeParameterDeclarations, true
                  ));

                  parameterDeclarations.push(
                      astFactory.createParameterDeclaration("value", astFactory.resolveType(indexSignatureDeclaration.type), null)
                  );
                  
                  members.push(astFactory.createMethodDeclaration(
                      "set", parameterDeclarations, astFactory.createTypeDeclaration("Unit"), typeParameterDeclarations, true
                  ));
                } else if (memberDeclaration.kind == ts.SyntaxKind.PropertyDeclaration) {
                    let propertyDeclaration = memberDeclaration as ts.PropertyDeclaration;
                    let convertedVariable = astFactory.convertVariable(
                        propertyDeclaration as (ts.VariableDeclaration & ts.PropertyDeclaration & ts.ParameterDeclaration)
                    );
                    if (convertedVariable != null) {
                      members.push(convertedVariable);
                    }
                } else if (memberDeclaration.kind == ts.SyntaxKind.MethodDeclaration) {
                  let functionDeclaration = memberDeclaration as (ts.FunctionDeclaration & ts.MethodDeclaration);
                  let convertedFunctionDeclaration = astFactory.convertMethodDeclaration(functionDeclaration);
                  if (convertedFunctionDeclaration != null) {
                    members.push(convertedFunctionDeclaration);
                  }
                } else if (memberDeclaration.kind == ts.SyntaxKind.Constructor) {
                  let constructor = memberDeclaration as ts.ConstructorDeclaration;

                  let params: Array<ParameterDeclaration> = [];

                  for (let parameter of constructor.parameters) {
                    if (parameter.modifiers) {
                      let isField = parameter.modifiers.some(modifier => modifier.kind == ts.SyntaxKind.PublicKeyword);
                      if (isField) {
                        let convertedVariable = astFactory.convertVariable(
                            parameter as (ts.VariableDeclaration & ts.PropertyDeclaration & ts.ParameterDeclaration)
                        );
                        if (convertedVariable != null) {
                          members.push(convertedVariable);
                        }
                      }
                    }

                    params.push(astFactory.createParamDeclaration(parameter));
                  }


                  let functionDeclaration = astFactory.createMethodDeclaration("@@CONSTRUCTOR",
                      params,
                      astFactory.createTypeDeclaration("______")
                      , astFactory.convertTypeParams(constructor.typeParameters));
                  members.push(functionDeclaration);
                }
              }
            }

            declarations.push(
                astFactory.createClassDeclaration(
                    classDeclaration.name.getText(),
                    members,
                    astFactory.convertTypeParams(classDeclaration.typeParameters)
                )
            );
          }

        } else if (ts.isFunctionDeclaration(statement)) {
          let convertedFunctionDeclaration = astFactory.convertFunctionDeclaration(statement as (ts.FunctionDeclaration & ts.MethodDeclaration));
          if (convertedFunctionDeclaration != null) {
            declarations.push(convertedFunctionDeclaration)
          }
        } else if (ts.isInterfaceDeclaration(statement)) {
            let interfaceDeclaration = statement as ts.InterfaceDeclaration;
            declarations.push(
                astFactory.createInterfaceDeclaration(
                    interfaceDeclaration.name.getText(), [],[]
                )
            )
        } else {
          console.log("SKIPPING ", statement.kind);
        }
      }
    }
  }

  return astFactory.createDocumentRoot(declarations)
}


if (typeof module != "undefined") {
  module.exports = main;
}
