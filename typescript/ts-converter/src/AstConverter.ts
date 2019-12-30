import * as ts from "typescript";
import {createLogger} from "./Logger";
import {uid} from "./uid";
import {
    Block,
    Declaration,
    DefinitionInfoDeclaration,
    Expression,
    HeritageClauseDeclaration,
    IdentifierDeclaration,
    MemberDeclaration,
    ModifierDeclaration,
    ModuleDeclaration,
    NameEntity,
    ParameterDeclaration,
    ReferenceEntity,
    SourceFileDeclaration,
    TypeDeclaration,
    TypeParameter
} from "./ast/ast";
import {AstFactory} from "./ast/AstFactory";
import {DeclarationResolver} from "./DeclarationResolver";
import {AstExpressionConverter} from "./ast/AstExpressionConverter";
import {ExportContext} from "./ExportContext";
import {AstVisitor} from "./AstVisitor";
import {tsInternals} from "./TsInternals";

export class AstConverter {
    private log = createLogger("AstConverter");
    private unsupportedDeclarations = new Set<Number>();

    constructor(
      private rootPackageName: NameEntity,
      private exportContext: ExportContext,
      private typeChecker: ts.TypeChecker,
      private declarationResolver: DeclarationResolver,
      private astFactory: AstFactory,
      private astVisitor: AstVisitor
    ) {
    }

    private astExpressionConverter = new AstExpressionConverter(this, this.astFactory);


    private registerDeclaration<T>(declaration: T, collection: Array<T>) {
        collection.push(declaration);
    }

    createSourceFileDeclaration(sourceFile: ts.SourceFile): SourceFileDeclaration {
        let resourceName = this.rootPackageName;

        let packageNameFragments = sourceFile.fileName.split("/");
        let sourceName = packageNameFragments[packageNameFragments.length - 1].replace(".d.ts", "");

        const declarations = this.convertStatements(sourceFile.statements, resourceName);

        let curDir = tsInternals.getDirectoryPath(sourceFile.fileName) + "/";

        let packageDeclaration = this.createModuleDeclaration(resourceName, declarations, this.convertModifiers(sourceFile.modifiers), [], uid(), sourceName, true);
        let referencedFiles = new Set<string>();
        sourceFile.referencedFiles.forEach(referencedFile => referencedFiles.add(curDir + referencedFile.fileName));

        if (sourceFile.resolvedTypeReferenceDirectiveNames instanceof Map) {
          for (let [_, referenceDirective] of sourceFile.resolvedTypeReferenceDirectiveNames) {
            if (referenceDirective && referenceDirective.hasOwnProperty("resolvedFileName")) {
                referencedFiles.add(tsInternals.normalizePath(referenceDirective.resolvedFileName));
            }
          }
        }

        sourceFile.forEachChild(node => {
            if (ts.isImportDeclaration(node)) {
              let symbol = this.typeChecker.getSymbolAtLocation(node.moduleSpecifier);
              if (symbol && symbol.valueDeclaration) {
                referencedFiles.add(tsInternals.normalizePath(symbol.valueDeclaration.getSourceFile().fileName));
              }
            }
        });

        for (let importDeclaration of sourceFile.imports) {
          const module = ts.getResolvedModule(sourceFile, importDeclaration.text);
          if (module && (typeof module.resolvedFileName == "string")) {
            referencedFiles.add(tsInternals.normalizePath(module.resolvedFileName));
          }
        }

        return this.astFactory.createSourceFileDeclaration(
            sourceFile.fileName,
            packageDeclaration,
            Array.from(referencedFiles)
        );
    }

    printDiagnostics() {
        this.log.debug("following declarations has been skipped: ");
        this.unsupportedDeclarations.forEach(id => {
            this.log.debug(`SKIPPED ${ts.SyntaxKind[id]} (${id})`);
        });
    }

    createModuleDeclaration(packageName: NameEntity, declarations: Declaration[], modifiers: Array<ModifierDeclaration>, definitionsInfo: Array<DefinitionInfoDeclaration>, uid: string, resourceName: string, root: boolean): ModuleDeclaration {
        return this.astFactory.createModuleDeclaration(packageName, declarations, modifiers, definitionsInfo, uid, resourceName, root);
    }

    createModuleDeclarationAsTopLevel(packageName: NameEntity, declarations: Declaration[], modifiers: Array<ModifierDeclaration>, definitionsInfo: Array<DefinitionInfoDeclaration>, uid: string, resourceName: string, root: boolean): Declaration {
        return this.astFactory.createModuleDeclarationAsTopLevel(packageName, declarations, modifiers, definitionsInfo, uid, resourceName, root);
    }

    convertName(name: ts.BindingName | ts.PropertyName): string | null {
        //TODO: this should be process at frontend
        if (ts.isNumericLiteral(name)) {
            return "`" + name.getText() + "`";
        } else if (ts.isStringLiteral(name)) {
            let text = name.getText();
            return text.substring(1, text.length - 1);
        } else if (ts.isIdentifier(name)) {
            return name.getText();
        }
        return null
    }

    convertPropertyDeclaration(nativePropertyDeclaration: (ts.PropertyDeclaration | ts.ParameterDeclaration)): MemberDeclaration | null {
        let name = this.convertName(nativePropertyDeclaration.name);

        if (name != null) {
            return this.astFactory.declareProperty(
              name,
              null,
              this.convertType(nativePropertyDeclaration.type),
              [],
              false,
              this.convertModifiers(nativePropertyDeclaration.modifiers)
            );
        }

        return null;
    }

    convertTypeParams(nativeTypeDeclarations: ts.NodeArray<ts.TypeParameterDeclaration> | undefined): Array<TypeParameter> {
        let typeParameterDeclarations: Array<TypeParameter> = [];

        if (nativeTypeDeclarations) {
            typeParameterDeclarations = nativeTypeDeclarations.map(typeParam => {
                const constraint = typeParam.constraint;
                let defaultValue = typeParam.default ? this.convertType(typeParam.default) : null;
                return this.astFactory.createTypeParam(this.astFactory.createIdentifierDeclarationAsNameEntity(typeParam.name.getText()), constraint ? [
                    this.convertType(constraint)
                ] : [], defaultValue)
            });
        }

        return typeParameterDeclarations;
    }

    convertTypeParamsToTokens(nativeTypeDeclarations: ts.NodeArray<ts.TypeParameterDeclaration> | undefined): Array<IdentifierDeclaration> {
        let typeParameterDeclarations: Array<IdentifierDeclaration> = [];

        if (nativeTypeDeclarations) {
            typeParameterDeclarations = nativeTypeDeclarations.map(typeParam => {
                return this.astFactory.createIdentifierDeclaration(typeParam.name.getText())
            });
        }

        return typeParameterDeclarations;
    }

    private getStatementsFromBlock(block: ts.Block): Array<Declaration> {
        let statements: Declaration[] = [];

        for (let statement of block.statements) {
            for (let decl of this.convertTopLevelStatement(statement)) {
                this.registerDeclaration(decl, statements)
            }
        }

        return statements;
    }

    convertBlock(block: ts.Block | null): Block | null {
        if (block) {
            let statements = this.getStatementsFromBlock(block);
            return this.astFactory.createBlockDeclaration(statements);
        } else {
            return null;
        }
    }

    convertBlockStatement(block: ts.Block): Declaration {
        let statements = this.getStatementsFromBlock(block);
        return this.astFactory.createBlockStatementDeclaration(statements);
    }

    convertFunctionDeclaration(functionDeclaration: ts.FunctionDeclaration): Declaration | null {

        let typeParameterDeclarations: Array<TypeParameter> = this.convertTypeParams(functionDeclaration.typeParameters);

        let parameterDeclarations = functionDeclaration.parameters
          .map(
            (param, count) => this.convertParameterDeclaration(param, count)
          );


        if (!functionDeclaration.name) {
            return null;
        }

        if (ts.isIdentifier(functionDeclaration.name)) {
            let uid = this.exportContext.getUID(functionDeclaration);
            let returnType = functionDeclaration.type ?
              this.convertType(functionDeclaration.type) : this.createTypeDeclaration("Unit");

            return this.astFactory.createFunctionDeclarationAsTopLevel(
              functionDeclaration.name ? functionDeclaration.name.getText() : "",
              parameterDeclarations,
              returnType,
              typeParameterDeclarations,
              this.convertModifiers(functionDeclaration.modifiers),
              this.convertBlock(functionDeclaration.body),
              uid
            );
        }

        return null;
    }

    convertModifiers(nativeModifiers: ts.NodeArray<ts.Modifier> | undefined): Array<ModifierDeclaration> {
        var res: Array<ModifierDeclaration> = [];

        if (nativeModifiers) {
            nativeModifiers.forEach(modifier => {
                if (modifier.kind == ts.SyntaxKind.StaticKeyword) {
                    this.registerDeclaration(this.astFactory.createModifierDeclaration("STATIC"), res);
                } else if (modifier.kind == ts.SyntaxKind.DeclareKeyword) {
                    this.registerDeclaration(this.astFactory.createModifierDeclaration("DECLARE"), res);
                } else if (modifier.kind == ts.SyntaxKind.ExportKeyword) {
                    this.registerDeclaration(this.astFactory.createModifierDeclaration("EXPORT"), res);
                } else if (modifier.kind == ts.SyntaxKind.DefaultKeyword) {
                    this.registerDeclaration(this.astFactory.createModifierDeclaration("DEFAULT"), res);
                }
            });
        }

        return res;
    }


    convertMethodSignatureDeclaration(declaration: ts.MethodSignature): MemberDeclaration | null {
        let typeParameterDeclarations: Array<TypeParameter> = this.convertTypeParams(declaration.typeParameters);

        let parameterDeclarations = declaration.parameters
          .map(
            (param, count) => this.convertParameterDeclaration(param, count)
          );

        if (ts.isIdentifier(declaration.name)) {
            return this.astFactory.createMethodSignatureDeclaration(
              declaration.name ? declaration.name.getText() : "",
              parameterDeclarations,
              declaration.type ?
                this.convertType(declaration.type) : this.createTypeDeclaration("Unit"),
              typeParameterDeclarations,
              !!declaration.questionToken,
              this.convertModifiers(declaration.modifiers)
            );
        }

        return null;
    }


    convertMethodDeclaration(declaration: ts.MethodSignature): MemberDeclaration | null {
        let typeParameterDeclarations: Array<TypeParameter> = this.convertTypeParams(declaration.typeParameters);

        let parameterDeclarations = declaration.parameters
          .map(
            (param, count) => this.convertParameterDeclaration(param, count)
          );


        if (ts.isIdentifier(declaration.name)) {
            return this.createMethodDeclaration(
              declaration.name ? declaration.name.getText() : "",
              parameterDeclarations,
              declaration.type ?
                this.convertType(declaration.type) : this.createTypeDeclaration("Unit"),
              typeParameterDeclarations,
              this.convertModifiers(declaration.modifiers),
              this.convertBlock(declaration.body),
            );
        }

        return null;
    }


    createMethodDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: TypeDeclaration, typeParams: Array<TypeParameter>, modifiers: Array<ModifierDeclaration>, body: Block | null): MemberDeclaration {
        // TODO: reintroduce method declaration
        return this.astFactory.createFunctionDeclarationAsMember(name, parameters, type, typeParams, modifiers, body, "__NO_UID__");
    }

    createTypeDeclaration(value: string, params: Array<TypeDeclaration> = [], typeReference: string | null = null): TypeDeclaration {
        return this.astFactory.createTypeReferenceDeclarationAsParamValue(this.astFactory.createIdentifierDeclarationAsNameEntity(value), params, null);
    }

    createParameterDeclaration(name: string, type: TypeDeclaration, initializer: Expression | null, vararg: boolean, optional: boolean): ParameterDeclaration {
        return this.astFactory.createParameterDeclaration(name, type, initializer, vararg, optional);
    }

    createProperty(value: string, initializer: Expression | null, type: TypeDeclaration, typeParams: Array<TypeParameter> = [], optional: boolean): MemberDeclaration {
        return this.astFactory.declareProperty(value, initializer, type, typeParams, optional, []);
    }


    createIntersectionType(params: Array<TypeDeclaration>) {
        return this.astFactory.createIntersectionTypeDeclaration(params);
    }

    convertEntityName(entityName: ts.EntityName): NameEntity {
        if (ts.isQualifiedName(entityName)) {
            return this.astFactory.createQualifiedNameDeclaration(
              this.convertEntityName(entityName.left),
              this.convertEntityName(entityName.right).getIdentifier()!
            )
        }

        return this.astFactory.createIdentifierDeclarationAsNameEntity(entityName.getText());
    }

    private convertTypeArguments(typeArguments: ts.NodeArray<ts.TypeNode> | undefined): Array<TypeDeclaration> {
        if (typeArguments == undefined) {
            return []
        } else {
            return typeArguments
              .map(argumentType => {
                  return this.convertType(argumentType)
              });
        }
    }

    convertType(type: ts.TypeNode | undefined): TypeDeclaration {
        if (type == undefined) {
            return this.createTypeDeclaration("Any")
        } else {
            this.astVisitor.visitType(type);

            if (type.kind == ts.SyntaxKind.VoidKeyword) {
                return this.createTypeDeclaration("Unit")
            } else if (ts.isArrayTypeNode(type)) {
                let arrayType = type as ts.ArrayTypeNode;
                return this.createTypeDeclaration("@@ArraySugar", [this.convertType(arrayType.elementType)])
            } else if (ts.isUnionTypeNode(type)) {
                let unionTypeNode = type as ts.UnionTypeNode;
                let params = unionTypeNode.types
                  .map(argumentType => this.convertType(argumentType));

                return this.astFactory.createUnionTypeDeclaration(params)
            } else if (ts.isIntersectionTypeNode(type)) {
                let intersectionTypeNode = type as ts.IntersectionTypeNode;
                let params = intersectionTypeNode.types
                  .map(argumentType => this.convertType(argumentType));

                return this.createIntersectionType(params);
            } else if (ts.isTypeReferenceNode(type)) {
                let params = this.convertTypeArguments(type.typeArguments);
                let entity = this.convertEntityName(type.typeName);

                let symbol = this.typeChecker.getSymbolAtLocation(type.typeName);
                let typeReference: ReferenceEntity | null = null;
                if (symbol) {
                    if (Array.isArray(symbol.declarations)) {
                        let declaration = symbol.declarations[0];

                        if (declaration) {
                            if (ts.isTypeParameterDeclaration(declaration)) {
                                return this.astFactory.createTypeParamReferenceDeclarationAsParamValue(entity);
                            }

                            if (ts.isImportSpecifier(declaration)) {
                                let typeOsSymbol = this.typeChecker.getDeclaredTypeOfSymbol(symbol);
                                if (typeOsSymbol && typeOsSymbol.symbol && Array.isArray(typeOsSymbol.symbol.declarations)) {
                                    let declarationFromSymbol = typeOsSymbol.symbol.declarations[0];
                                    //TODO: encountered in @types/express, need to work on a separate test case
                                    let uidContext =  (declarationFromSymbol.parent && ts.isTypeAliasDeclaration(declarationFromSymbol.parent))?
                                                            declarationFromSymbol.parent : declarationFromSymbol;
                                    typeReference = this.astFactory.createReferenceEntity(this.exportContext.getUID(uidContext));
                                }
                            } else {
                                typeReference = this.astFactory.createReferenceEntity(this.exportContext.getUID(declaration));
                            }

                        }
                    }
                }

                return this.astFactory.createTypeReferenceDeclarationAsParamValue(
                  entity,
                  params,
                  typeReference
                );
            } else if (type.kind == ts.SyntaxKind.ParenthesizedType) {
                let parenthesizedTypeNode = type as ts.ParenthesizedTypeNode;
                return this.convertType(parenthesizedTypeNode.type);
            } else if (type.kind == ts.SyntaxKind.NullKeyword) {
                return this.createTypeDeclaration("null")
            } else if (type.kind == ts.SyntaxKind.UndefinedKeyword) {
                return this.createTypeDeclaration("undefined")
            } else if (type.kind == ts.SyntaxKind.StringKeyword) {
                return this.createTypeDeclaration("string")
            } else if (type.kind == ts.SyntaxKind.BooleanKeyword) {
                return this.createTypeDeclaration("boolean")
            } else if (type.kind == ts.SyntaxKind.NumberKeyword) {
                return this.createTypeDeclaration("number")
            } else if (type.kind == ts.SyntaxKind.AnyKeyword) {
                return this.createTypeDeclaration("any")
            } else if (type.kind == ts.SyntaxKind.FunctionType) {
                const functionDeclaration = type as ts.FunctionTypeNode;
                let parameterDeclarations = functionDeclaration.parameters.map(
                  (param, count) => this.convertParameterDeclaration(param, count)
                );
                return this.astFactory.createFunctionTypeDeclaration(parameterDeclarations, this.convertType(functionDeclaration.type))
            } else if (ts.isTypeLiteralNode(type)) {
                return this.convertTypeLiteralToObjectLiteralDeclaration(type as ts.TypeLiteralNode)
            } else if (ts.isThisTypeNode(type)) {
                return this.astFactory.createThisTypeDeclaration()
            } else if (ts.isLiteralTypeNode(type)) {
                // TODO: we need to pass information on literal futher and convert it in some lowering
                if ((type.literal.kind == ts.SyntaxKind.TrueKeyword) || (type.literal.kind == ts.SyntaxKind.FalseKeyword)) {
                    return this.createTypeDeclaration("boolean");
                } else {
                    return this.astFactory.createStringLiteralDeclaration(type.literal.getText());
                }
            } else if (ts.isTupleTypeNode(type)) {
                return this.astFactory.createTupleDeclaration(type.elementTypes.map(elementType => this.convertType(elementType)))
            } else if (ts.isTypePredicateNode(type)) {
                return this.createTypeDeclaration("boolean");
            } else if (type.kind == ts.SyntaxKind.ObjectKeyword) {
                return this.astFactory.createUnionTypeDeclaration([
                      this.createTypeDeclaration("any"),
                      this.createTypeDeclaration("undefined")
                  ]
                )
            } else {
                // TODO: use raiseConcern for this
                this.unsupportedDeclarations.add(type.kind);
                return this.createTypeDeclaration("any");
            }
        }
    }

    convertParameterDeclarations(parameters: ts.NodeArray<ts.ParameterDeclaration>): Array<ParameterDeclaration> {
        return parameters.map((parameter, count) => this.convertParameterDeclaration(parameter, count));
    }

    convertParameterDeclaration(param: ts.ParameterDeclaration, index: number): ParameterDeclaration {
        let initializer: Expression | null = null;

        if (param.initializer != null) {
            initializer = this.astExpressionConverter.convertExpression(param.initializer)
        }

        let paramType = this.convertType(param.type);

        let name = ts.isIdentifier(param.name) ? param.name.getText() : `__${index}`;

        return this.createParameterDeclaration(
          name,
          paramType,
          initializer,
          !!param.dotDotDotToken,
          !!param.questionToken
        );
    }

    convertTypeElementToMethodSignatureDeclaration(methodDeclaration: ts.MethodSignature): MemberDeclaration | null {
        let convertedMethodDeclaration = this.convertMethodSignatureDeclaration(methodDeclaration);
        if (convertedMethodDeclaration != null) {
            return convertedMethodDeclaration
        }

        return null;
    }

    convertTypeElementToMemberDeclaration(methodDeclaration: ts.MethodSignature): MemberDeclaration | null {
        let convertedMethodDeclaration = this.convertMethodDeclaration(methodDeclaration);
        if (convertedMethodDeclaration != null) {
            return convertedMethodDeclaration
        }

        return null;
    }

    convertPropertySignature(node: ts.PropertySignature): MemberDeclaration | null {
        let name = this.convertName(node.name);

        if (name !== null) {
            return this.createProperty(name, null, this.convertType(node.type), [], !!node.questionToken);
        }

        return null;
    }

    convertIndexSignature(indexSignatureDeclaration: ts.IndexSignatureDeclaration): Array<MemberDeclaration> {
        let res: Array<MemberDeclaration> = [];
        let parameterDeclarations = indexSignatureDeclaration.parameters
          .map(
            (param, count) => this.convertParameterDeclaration(param, count)
          );

        this.registerDeclaration(
          this.astFactory.createIndexSignatureDeclaration(
            parameterDeclarations,
            this.convertType(indexSignatureDeclaration.type)
          ), res
        );

        return res;
    }

    convertTypeElementToInterfaceMemberDeclarations(member: ts.TypeElement): Array<MemberDeclaration> {
        let res: Array<MemberDeclaration> = [];

        if (ts.isMethodSignature(member)) {
            let methodDeclaration = this.convertTypeElementToMethodSignatureDeclaration(member);
            if (methodDeclaration) {
                this.registerDeclaration(methodDeclaration, res);
            }
        } else if (ts.isPropertySignature(member)) {
            let propertySignatureDeclaration = this.convertPropertySignature(member);
            if (propertySignatureDeclaration !== null) {
                this.registerDeclaration(
                  propertySignatureDeclaration, res
                );
            }
        } else if (ts.isIndexSignatureDeclaration(member)) {
            this.convertIndexSignature(member as ts.IndexSignatureDeclaration).forEach(member =>
              this.registerDeclaration(member, res)
            );
        } else if (ts.isCallSignatureDeclaration(member)) {
            this.registerDeclaration(
              this.astFactory.createCallSignatureDeclaration(
                this.convertParameterDeclarations(member.parameters),
                member.type ? this.convertType(member.type) : this.createTypeDeclaration("Unit"),
                this.convertTypeParams(member.typeParameters)
              ), res
            );
        }

        return res;
    }

    convertMembersToInterfaceMemberDeclarations(members: ts.NodeArray<ts.TypeElement>): Array<MemberDeclaration> {
        let res: Array<MemberDeclaration> = [];
        members.map(member => {
            this.convertTypeElementToInterfaceMemberDeclarations(member).map(memberDeclaration => {
                this.registerDeclaration(memberDeclaration, res);
            });
        });

        return res;
    }

    convertClassElementsToMembers(classDeclarationMembers: ts.NodeArray<ts.ClassElement> | null): Array<MemberDeclaration> {
        if (classDeclarationMembers == null) {
            return [];
        }

        let members: Array<MemberDeclaration> = [];

        for (let memberDeclaration of classDeclarationMembers) {
            if (ts.isIndexSignatureDeclaration(memberDeclaration)) {
                this.convertIndexSignature(memberDeclaration as ts.IndexSignatureDeclaration).map(member => {
                    this.registerDeclaration(member, members);
                });
            } else if (ts.isPropertyDeclaration(memberDeclaration)) {
                let propertyDeclaration = this.convertPropertyDeclaration(
                  memberDeclaration as ts.PropertyDeclaration
                );
                if (propertyDeclaration != null) {
                    this.registerDeclaration(propertyDeclaration, members);
                }
            } else if (ts.isMethodDeclaration(memberDeclaration)) {
                let methodDeclaration = memberDeclaration as (ts.FunctionDeclaration & ts.MethodDeclaration & ts.MethodSignature);
                let convertedMethodDeclaration = this.convertMethodDeclaration(methodDeclaration);
                if (convertedMethodDeclaration != null) {
                    this.registerDeclaration(convertedMethodDeclaration, members);
                }
            } else if (memberDeclaration.kind == ts.SyntaxKind.Constructor) {
                this.convertConstructorDeclaration(memberDeclaration as ts.ConstructorDeclaration).map(member => {
                    this.registerDeclaration(member, members);
                });
            }
        }

        return members;
    }


    convertTypeLiteralToInterfaceDeclaration(uid: string, name: string, typeLiteral: ts.TypeLiteralNode, typeParams: ts.NodeArray<ts.TypeParameterDeclaration> | undefined): Declaration {
        return this.astFactory.createInterfaceDeclaration(
          this.astFactory.createIdentifierDeclarationAsNameEntity(name),
          this.convertMembersToInterfaceMemberDeclarations(typeLiteral.members),
          this.convertTypeParams(typeParams),
          [],
          [],
          uid
        );
    }

    convertTypeLiteralToObjectLiteralDeclaration(typeLiteral: ts.TypeLiteralNode): TypeDeclaration {
        return this.astFactory.createObjectLiteral(
          this.convertMembersToInterfaceMemberDeclarations(typeLiteral.members),
          this.exportContext.getUID(typeLiteral)
        );
    }

    convertConstructorDeclaration(constructorDeclaration: ts.ConstructorDeclaration): Array<MemberDeclaration> {

        let params: Array<ParameterDeclaration> = [];

        let res: Array<MemberDeclaration> = [];

        constructorDeclaration.parameters.forEach((parameter, count) => {
            if (parameter.modifiers) {
                let isField = parameter.modifiers.some(modifier => modifier.kind == ts.SyntaxKind.PublicKeyword);
                if (isField) {
                    let convertedVariable = this.convertPropertyDeclaration(
                      parameter as ts.ParameterDeclaration
                    );
                    if (convertedVariable != null) {
                        this.registerDeclaration(convertedVariable, res);
                    }
                }
            }

            this.registerDeclaration(this.convertParameterDeclaration(parameter, count), params);
        });

        this.registerDeclaration(
            this.astFactory.createConstructorDeclaration(
                params,
                this.convertTypeParams(constructorDeclaration.typeParameters),
                this.convertModifiers(constructorDeclaration.modifiers),
                this.convertBlock(constructorDeclaration.body)
            ),
            res
        );

        return res;
    }

    private convertTypeAliasDeclaration(declaration: ts.TypeAliasDeclaration): Declaration {
        return this.astFactory.createTypeAliasDeclaration(
          this.convertEntityName(declaration.name),
          this.convertTypeParamsToTokens(declaration.typeParameters),
          this.convertType(declaration.type),
          this.exportContext.getUID(declaration)
        )
    }

    private convertPropertyAccessExpression(propertyAccessExpression: ts.PropertyAccessExpression): NameEntity {
        let convertedExpression: NameEntity | null;
        let name = this.astFactory.createIdentifierDeclaration(propertyAccessExpression.name.text);

        if (ts.isIdentifier(propertyAccessExpression.expression)) {
            convertedExpression = this.astFactory.createIdentifierDeclarationAsNameEntity(propertyAccessExpression.expression.text);
        } else if (ts.isPropertyAccessExpression(propertyAccessExpression.expression)) {
            convertedExpression = this.convertPropertyAccessExpression(propertyAccessExpression.expression);
        } else {
            // TODO: we can not have errors to be honest
            throw new Error("never supposed to be there")
        }

        return this.astFactory.createQualifiedNameDeclaration(convertedExpression, name);
    }

    convertHeritageClauses(heritageClauses: ts.NodeArray<ts.HeritageClause> | undefined, parent: ts.Node): Array<HeritageClauseDeclaration> {
        let parentEntities: Array<HeritageClauseDeclaration> = [];

        if (heritageClauses) {
            for (let heritageClause of heritageClauses) {

                let extending = heritageClause.token == ts.SyntaxKind.ExtendsKeyword;

                for (let type of heritageClause.types) {
                    let typeArguments: Array<TypeDeclaration> = [];

                    if (type.typeArguments) {
                        for (let typeArgument of type.typeArguments) {
                            this.registerDeclaration(this.convertType(typeArgument), typeArguments)
                        }
                    }

                    let expression = type.expression;

                    let name: NameEntity | null = null;
                    if (ts.isPropertyAccessExpression(expression)) {
                        name = this.convertPropertyAccessExpression(expression);
                    } else if (ts.isIdentifier(expression)) {
                        name = this.astFactory.createIdentifierDeclarationAsNameEntity(expression.getText());
                    }

                    let uid: string | null = null;

                    let symbol = this.typeChecker.getSymbolAtLocation(type.expression);
                    if (symbol) {
                        if (Array.isArray(symbol.declarations)) {
                            let declaration = symbol.declarations[0];
                            if (declaration) {
                                this.astVisitor.visitType(declaration);
                                uid = this.exportContext.getUID(declaration);
                            }
                        }
                    }

                    let parentUid = this.exportContext.getUID(parent);

                    if (parentUid != uid) {
                        let typeReference = uid ? this.astFactory.createReferenceEntity(uid) : null;

                        if (name) {
                            this.registerDeclaration(
                              this.astFactory.createHeritageClauseDeclaration(
                                name,
                                typeArguments,
                                extending,
                                typeReference,
                              ), parentEntities
                            );
                        }
                    }


                }
            }
        }

        return parentEntities
    }

    convertClassDeclaration(statement: ts.ClassDeclaration): Declaration | null {
        if (statement.name == undefined) {
            return null;
        }

        return this.astFactory.createClassDeclarationAsTopLevel(
          this.astFactory.createIdentifierDeclarationAsNameEntity(statement.name.getText()),
          this.convertClassElementsToMembers(statement.members),
          this.convertTypeParams(statement.typeParameters),
          this.convertHeritageClauses(statement.heritageClauses, statement),
          this.convertModifiers(statement.modifiers),
          this.exportContext.getUID(statement)
        );
    }

    private convertDefinitions(kind: ts.SyntaxKind, name: ts.Node): Array<DefinitionInfoDeclaration> {
        let definitionInfos = this.declarationResolver.resolve(kind, name);

        let definitionsInfoDeclarations: Array<DefinitionInfoDeclaration> = [];
        if (definitionInfos) {
            definitionsInfoDeclarations = definitionInfos.map((definitionInfo) => {
                return this.astFactory.createDefinitionInfoDeclaration(definitionInfo.fileName);
            });
        }

        return definitionsInfoDeclarations;
    }

    convertInterfaceDeclaration(statement: ts.InterfaceDeclaration, computeDefinitions: boolean = true): Declaration {
        return this.astFactory.createInterfaceDeclaration(
          this.astFactory.createIdentifierDeclarationAsNameEntity(statement.name.getText()),
          this.convertMembersToInterfaceMemberDeclarations(statement.members),
          this.convertTypeParams(statement.typeParameters),
          this.convertHeritageClauses(statement.heritageClauses, statement),
          computeDefinitions ? this.convertDefinitions(ts.SyntaxKind.InterfaceDeclaration, statement.name) : [],
          this.exportContext.getUID(statement)
        );
    }

    convertIterationStatement(statement: ts.Node): Declaration | null {
        let decl: Declaration | null = null;

        let body = this.convertTopLevelStatement(statement.statement);

        if (ts.isWhileStatement(statement)) {
            decl = this.astFactory.createWhileStatement(
                this.astExpressionConverter.convertExpression(statement.expression),
                body
            )
        }

        //TODO convert other iteration statements than while statement

        return decl
    }

    convertTopLevelStatement(statement: ts.Node): Array<Declaration> {
        let res: Array<Declaration> = [];

        if (ts.isEnumDeclaration(statement)) {
            let enumTokens = statement.members.map(member =>
              this.astFactory.createEnumTokenDeclaration(
                member.name.getText(),
                member.initializer ? member.initializer.getText() : ""
              ));

            res.push(this.astFactory.createEnumDeclaration(
              statement.name.getText(),
              enumTokens,
              this.exportContext.getUID(statement)
            ));
        } else if (ts.isVariableStatement(statement)) {
            for (let declaration of statement.declarationList.declarations) {
                res.push(this.astFactory.declareVariable(
                  declaration.name.getText(),
                  this.convertType(declaration.type),
                  this.convertModifiers(statement.modifiers),
                  declaration.initializer == null ? null : this.astExpressionConverter.convertExpression(declaration.initializer),
                  this.exportContext.getUID(declaration)
                ));
            }
        } else if (ts.isExpressionStatement(statement)) {
            res.push(this.astFactory.createExpressionStatement(
                this.astExpressionConverter.convertExpression(statement.expression)
            ));
        } else if (ts.isIfStatement(statement)) {
            res.push(this.astFactory.createIfStatement(
                this.astExpressionConverter.convertExpression(statement.expression),
                this.convertTopLevelStatement(statement.thenStatement),
                statement.elseStatement ? this.convertTopLevelStatement(statement.elseStatement) : null
            ))
        } else if (ts.isIterationStatement(statement)) {
            let iterationStatement = this.convertIterationStatement(statement);

            if (iterationStatement) {
                res.push(iterationStatement)
            }
        } else if (ts.isReturnStatement(statement)) {
            res.push(this.astFactory.createReturnStatement(
                statement.expression ? this.astExpressionConverter.convertExpression(statement.expression) : null
            ));
        } else if (ts.isThrowStatement(statement)) {
            res.push(this.astFactory.createThrowStatement(
                statement.expression ? this.astExpressionConverter.convertExpression(statement.expression) : null
            ))
        } else if (ts.isBlock(statement)) {
            let block = this.convertBlockStatement(statement);
            if (block) {
                res.push(block)
            }
        } else if (ts.isTypeAliasDeclaration(statement)) {
            if (ts.isTypeLiteralNode(statement.type)) {
                res.push(this.convertTypeLiteralToInterfaceDeclaration(
                  this.exportContext.getUID(statement),
                  statement.name.getText(),
                  statement.type as ts.TypeLiteralNode,
                  statement.typeParameters
                ));
            } else {
                res.push(this.convertTypeAliasDeclaration(statement));
            }
        } else if (ts.isClassDeclaration(statement)) {
            let classDeclaration = this.convertClassDeclaration(statement);

            if (classDeclaration != null) {
                res.push(classDeclaration);
            }
        } else if (ts.isFunctionDeclaration(statement)) {
            let convertedFunctionDeclaration = this.convertFunctionDeclaration(statement);
            if (convertedFunctionDeclaration != null) {
                res.push(convertedFunctionDeclaration);
            }
        } else if (ts.isInterfaceDeclaration(statement)) {
            res.push(this.convertInterfaceDeclaration(statement));
        } else if (ts.isExportAssignment(statement)) {
            let expression = statement.expression;
            if (ts.isIdentifier(expression) || ts.isPropertyAccessExpression(expression)) {
                let symbol = this.typeChecker.getSymbolAtLocation(expression);

                if (symbol) {

                    if (symbol.flags & ts.SymbolFlags.Alias) {
                        symbol = this.typeChecker.getAliasedSymbol(symbol);
                    }

                    if (Array.isArray(symbol.declarations) && symbol.declarations.length > 0) {
                        let declaration = symbol.declarations[0];

                        let uid = this.exportContext.getUID(declaration);
                        res.push(this.astFactory.createExportAssignmentDeclaration(
                          uid, !!statement.isExportEquals
                        ));
                    }
                }
            } else {
                this.log.info(`skipping unknown expression assignment: [${expression.kind}]`);
            }
        } else if (ts.isImportEqualsDeclaration(statement)) {
            if (ts.isEntityName(statement.moduleReference)) {
                let moduleReferenceDeclaration = this.convertEntityName(statement.moduleReference);
                let uid = ts.isModuleBlock(statement.parent) ? this.exportContext.getUID(statement.parent.parent) : this.exportContext.getUID(statement.parent);

                res.push(this.astFactory.createImportEqualsDeclaration(
                  statement.name.getText(),
                  moduleReferenceDeclaration,
                  uid
                ));
            } else {
                this.log.info(`skipping external module reference ${statement.moduleReference.getText()}, kind: ${statement.moduleReference.kind}`)
            }
        } else {
            this.unsupportedDeclarations.add(statement.kind);
        }

        return res;
    }

    private convertStatement(statement: ts.Node, resourceName: NameEntity): Array<Declaration> {
        let res: Array<Declaration> = this.convertTopLevelStatement(statement);

        if (ts.isModuleDeclaration(statement)) {
            for (let moduleDeclaration of this.convertModule(statement, resourceName)) {
                res.push(moduleDeclaration);
            }
        }

        return res;
    }

    private convertStatements(statements: ts.NodeArray<ts.Node>, resourceName: NameEntity): Array<Declaration> {
        const declarations: Declaration[] = [];
        for (let statement of statements) {
            for (let decl of this.convertStatement(statement, resourceName)) {
                this.registerDeclaration(decl, declarations)
            }
        }

        return declarations;
    }

    private resolveAmbientModuleName(moduleDeclaration: ts.ModuleDeclaration): string {
        if (ts.isNonGlobalAmbientModule(moduleDeclaration) && ts.isExternalModuleAugmentation(moduleDeclaration) && ts.isExternalModuleAugmentation(moduleDeclaration)) {
            let moduleSymbol = this.typeChecker.getSymbolAtLocation(moduleDeclaration.name);

            if (moduleSymbol && Array.isArray(moduleSymbol.declarations) && moduleSymbol.declarations[0]) {
                return moduleSymbol.declarations[0].name.getText();
            }
        }

        return moduleDeclaration.name.getText();
    }

    convertModule(module: ts.ModuleDeclaration, resourceName: NameEntity): Array<Declaration> {
        let definitionInfos = this.convertDefinitions(ts.SyntaxKind.ModuleDeclaration, module);

        const declarations: Declaration[] = [];
        if (module.body) {
            let definitionsInfoDeclarations: Array<DefinitionInfoDeclaration> = [];
            if (definitionInfos) {
                definitionsInfoDeclarations = definitionInfos.map(definitionInfo => {
                    return this.astFactory.createDefinitionInfoDeclaration(definitionInfo.getFilename());
                });
            }

            let body = module.body;
            let modifiers = this.convertModifiers(module.modifiers);
            let uid = this.exportContext.getUID(module);
            let sourceNameFragment = this.resolveAmbientModuleName(module);

            if (ts.isModuleBlock(body)) {
                let moduleDeclarations = this.convertStatements(body.statements, resourceName);
                this.registerDeclaration(this.createModuleDeclarationAsTopLevel(this.astFactory.createIdentifierDeclarationAsNameEntity(sourceNameFragment), moduleDeclarations, modifiers, definitionsInfoDeclarations, uid, sourceNameFragment, false), declarations);
            } else if (ts.isModuleDeclaration(body)) {
                this.registerDeclaration(this.createModuleDeclarationAsTopLevel(this.astFactory.createIdentifierDeclarationAsNameEntity(sourceNameFragment), this.convertModule(body, resourceName), modifiers, definitionsInfoDeclarations, uid, sourceNameFragment, false), declarations);
            }
        }
        return declarations
    }

}