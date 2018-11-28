/// <reference path="../node_modules/typescript/lib/typescriptServices.d.ts"/>
/// <reference path="../node_modules/typescript/lib/tsserverlibrary.d.ts"/>



if (typeof ts == "undefined") {
  (global as any).ts = require("typescript/lib/tsserverlibrary");
}

declare function print(...arg: any[]): void;

if (typeof console == "undefined") {
  (global as any).console = {
    log: (...arg: any[]) => {
      print(arg);
    }
  }
}

import fromString = ts.ScriptSnapshot.fromString;

interface FileResolver {
  resolve(fileName: string): string;
}

class SomeLanguageServiceHost implements ts.LanguageServiceHost {

  constructor(
    public fileResolver: FileResolver,
    private knownFiles = new Set<string>(),
    private currentDirectory: string = "",
  ) {
  }

  getCompilationSettings(): ts.CompilerOptions {
    return ts.getDefaultCompilerOptions();
  }

  getScriptFileNames(): string[] {
    var res: string[] = [];

    this.knownFiles.forEach((v1, v2, s) => {
      let item = v1;
      res.push(item);
    })

    return res;
  }

  getScriptVersion(fileName: string): string {
    return "0";
  }

  getDefaultLibFileName(options: ts.CompilerOptions): string {
    return "./build/resources/typescript/lib/lib.d.ts";
  }

  getCurrentDirectory(): string {
    return this.currentDirectory;
  }

  getScriptSnapshot(fileName: string): ts.IScriptSnapshot | undefined {
    var contents = this.fileResolver.resolve(fileName);
    return fromString(contents);
  }

  log(message: string): void {
    console.log(message);
  }

  register(knownFile: string) {
    this.knownFiles.add(knownFile);
  }
}


function resolveType(astFactory: AstFactory, type: ts.TypeNode | undefined) : TypeDeclaration {
  if (type == undefined) {
    return astFactory.createTypeDeclaration("Unit")
  } else {
    if (ts.isArrayTypeNode(type)) {
      let arrayType = type as ts.ArrayTypeNode;
      return astFactory.createGenericTypeDeclaration("@@ArraySugar", [
        resolveType(astFactory, arrayType.elementType)
      ] as Array<TypeDeclaration>)
    } else {
      if (ts.isUnionTypeNode(type)) {
        let unionTypeNode = type as ts.UnionTypeNode;
        let params = unionTypeNode.types
                      .map(argumentType => resolveType(astFactory, argumentType)) as Array<TypeDeclaration>;

        return astFactory.createGenericTypeDeclaration("@@Union",params)
      } else if (type.kind == ts.SyntaxKind.TypeReference) {
        let typeReferenceNode = type as ts.TypeReferenceNode
        if (typeof typeReferenceNode.typeArguments != "undefined") {
            let params = typeReferenceNode.typeArguments
              .map(argumentType => resolveType(astFactory, argumentType)) as Array<TypeDeclaration>;

            return astFactory.createGenericTypeDeclaration(typeReferenceNode.typeName.getText(), params)
        } else {
            return astFactory.createTypeDeclaration(typeReferenceNode.typeName.getText())
        }
      } else if (type.kind == ts.SyntaxKind.ParenthesizedType) {
        let parenthesizedTypeNode = type as ts.ParenthesizedTypeNode;
        return resolveType(astFactory, parenthesizedTypeNode.type);
        return astFactory.createTypeDeclaration("__PARENTHESIZED__")
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
        console.log("UNKNOWN ", type.kind)
        return astFactory.createTypeDeclaration("__UNKNOWN__")
      }
    }
  }
}

function main(astFactory: AstFactory, fileResolver: FileResolver, fileName: string)  {
  let documentRegistry = ts.createDocumentRegistry();

  let host= new SomeLanguageServiceHost(fileResolver);
  host.register(fileName);

  let languageService = ts.createLanguageService(host, documentRegistry);

  var program = languageService.getProgram();

  var declarations: Declaration[] = [];

  if (program != null) {
    var sourceFile = program.getSourceFile(fileName)

    if (sourceFile != null) {

      for (let statement of sourceFile.statements) {
        if (ts.isVariableStatement(statement)) {
          const variableStatment = statement as ts.VariableStatement;

          for (let declaration of variableStatment.declarationList.declarations) {

            declarations.push(astFactory.declareVariable(
              declaration.name.getText(),
              resolveType(astFactory, declaration.type)
            ));
          }
        } else if (ts.isClassDeclaration(statement)) {
          const classDeclaration = statement as ts.ClassDeclaration;
          if (classDeclaration.name != undefined) {
            declarations.push(astFactory.declareVariable(
              classDeclaration.name.getText(),
              astFactory.createTypeDeclaration("@@CLASS")
            ))
          }

        } else if (ts.isFunctionDeclaration(statement)) {
          const functionDeclaration = statement as ts.FunctionDeclaration;

          let parameterDeclarations = functionDeclaration.parameters.map(
            param => astFactory.createParameterDeclaration(param.name.getText(), resolveType(astFactory, param.type))
          );

          if (functionDeclaration.name != null) {
            declarations.push(
              astFactory.createFunctionDeclaration(
                functionDeclaration.name.escapedText.toString(),
                parameterDeclarations,
                resolveType(astFactory, functionDeclaration.type)
              )
            )
          }
        } else {
          console.log("SKIPPING ", statement.kind);
        }
      }
    }
  }

  return astFactory.createAstTree(declarations)
}


if (typeof module != "undefined") {
  module.exports = main;
}
