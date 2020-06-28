import * as ts from "typescript";
import {createLogger} from "./Logger";
import {uid} from "./uid";
import {
  BindingElementDeclaration,
  Block,
  Declaration,
  DefinitionInfoDeclaration,
  Expression,
  HeritageClauseDeclaration,
  IdentifierDeclaration,
  ImportClauseDeclaration,
  ImportSpecifierDeclaration,
  MemberDeclaration,
  ModifierDeclaration,
  ModuleDeclaration,
  NameEntity,
  ParameterDeclaration,
  ReferenceEntity,
  SourceFileDeclaration,
  StatementDeclaration,
  TypeDeclaration,
  TypeParameter
} from "./ast/ast";
import {AstFactory} from "./ast/AstFactory";
import {DeclarationResolver} from "./DeclarationResolver";
import {AstExpressionConverter} from "./ast/AstExpressionConverter";
import {ExportContext, resolveDeclarations} from "./ExportContext";
import {tsInternals} from "./TsInternals";
import {
  CaseDeclarationProto,
  ModifierDeclarationProto,
  ModuleDeclarationProto,
  ReferenceClauseDeclarationProto,
  ReferenceDeclarationProto,
  TopLevelDeclarationProto
} from "declarations";
import MODULE_KIND = ModuleDeclarationProto.MODULE_KIND;
import MODULE_KINDMap = ModuleDeclarationProto.MODULE_KINDMap;
import MODIFIER_KIND = ModifierDeclarationProto.MODIFIER_KIND;

export class AstConverter {
  private log = createLogger("AstConverter");
  private unsupportedDeclarations = new Set<Number>();

  constructor(
    private exportContext: ExportContext,
    private typeChecker: ts.TypeChecker,
    private declarationResolver: DeclarationResolver,
    private astFactory: AstFactory,
    private isLibNode: (node: ts.Node) => boolean
  ) {
  }

  private astExpressionConverter = new AstExpressionConverter(this, this.astFactory);

  private resolveModulePath(node: ts.Expression): string | null {
    const module = ts.getResolvedModule(node.getSourceFile(), node.getText());
    if (module && (typeof module.resolvedFileName == "string")) {
      return tsInternals.normalizePath(module.resolvedFileName);
    }
    return null;
  }

  private getReferences(sourceFile: ts.SourceFile): Array<ReferenceClauseDeclarationProto> {
    let curDir = tsInternals.getDirectoryPath(sourceFile.fileName);
    let visitedReferences = new Set<string>();

    let referencedFiles = new Array<ReferenceClauseDeclarationProto>();
    sourceFile.referencedFiles.forEach(referencedFile => {
      if (!visitedReferences.has(referencedFile.fileName)) {
        visitedReferences.add(referencedFile.fileName);
        referencedFiles.push(this.astFactory.createReferenceClause(referencedFile.fileName, ts.getNormalizedAbsolutePath(referencedFile.fileName, curDir)));
      }
    });

    if (sourceFile.resolvedTypeReferenceDirectiveNames instanceof Map) {
      for (let [_, referenceDirective] of sourceFile.resolvedTypeReferenceDirectiveNames) {
        if (referenceDirective && referenceDirective.hasOwnProperty("resolvedFileName")) {
          if (!visitedReferences.has(referenceDirective.resolvedFileName)) {
            visitedReferences.add(referenceDirective.resolvedFileName);
            referencedFiles.push(this.astFactory.createReferenceClause(_, tsInternals.normalizePath(referenceDirective.resolvedFileName)));
          }
        }
      }
    }

    //TODO: Consider to place it to getImports
    for (let importDeclaration of sourceFile.imports) {
      const modulePath = this.resolveModulePath(importDeclaration);
      if (modulePath) {
        if (!visitedReferences.has(modulePath)) {
          visitedReferences.add(modulePath);
          referencedFiles.push(this.astFactory.createReferenceClause("_", modulePath));
        }
      }
    }

    return referencedFiles;
  }

  private getImports(sourceFile: ts.SourceFile): Array<ImportClauseDeclaration> {
    let imports: Array<ImportClauseDeclaration> = [];
    sourceFile.forEachChild(node => {
      if (ts.isImportDeclaration(node)) {
        if (node.importClause) {
          let namedBindings = node.importClause.namedBindings;
          if (namedBindings) {
            let importClause: ImportClauseDeclaration | null;
            if (ts.isNamespaceImport(namedBindings)) {
              importClause = this.astFactory.createNamespaceImportClause(namedBindings.name.getText());
            } else {
              importClause = this.astFactory.createNamedImportsClause(namedBindings.elements.map(importSpecifier =>
                this.createImportSpecifier(importSpecifier)
              ));
            }
            if (importClause) {
              let referenceFile = this.resolveModulePath(node.moduleSpecifier);
              if (referenceFile) {
                importClause.setReferencedfile(referenceFile);
              }

              imports.push(importClause);
            }
          }
        }
      }
    });

    return imports;
  }

  private createModuleFromSourceFile(sourceFile: ts.SourceFile, filter?: (node: ts.Node) => boolean): ModuleDeclaration {
    let packageNameFragments = sourceFile.fileName.split("/");
    let sourceName = packageNameFragments[packageNameFragments.length - 1].replace(".d.ts", "");

    let statements = filter ? sourceFile.statements.filter(filter) : sourceFile.statements;

    return this.astFactory.createModuleDeclaration(
      null,
      this.getImports(sourceFile),
      this.getReferences(sourceFile),
      this.convertStatements(statements),
      this.convertModifiers(sourceFile.modifiers),
      uid(),
      sourceName,
      [],
      sourceFile.isDeclarationFile ? MODULE_KIND.DECLARATION_FILE : MODULE_KIND.SOURCE_FILE,
      this.isLibNode(sourceFile)
    );
  }

  createSourceFileDeclaration(sourceFile: ts.SourceFile, filter?: (node: ts.Node) => boolean): SourceFileDeclaration {
    return this.astFactory.createSourceFileDeclaration(
      sourceFile.fileName,
      this.createModuleFromSourceFile(sourceFile, filter)
    );
  }

  printDiagnostics() {
    this.log.debug("following declarations has been skipped: ");
    this.unsupportedDeclarations.forEach(id => {
      this.log.debug(`SKIPPED ${ts.SyntaxKind[id]} (${id})`);
    });
  }

  private createModuleDeclarationAsTopLevel(packageName: NameEntity, imports: Array<ImportClauseDeclaration>, references: Array<ReferenceClauseDeclarationProto>, declarations: Iterable<Declaration>, modifiers: Array<ModifierDeclaration>, uid: string, resourceName: string, definitions: Array<DefinitionInfoDeclaration>, kind: MODULE_KINDMap[keyof MODULE_KINDMap], isLib: boolean): TopLevelDeclarationProto {
    return this.astFactory.createModuleDeclarationAsTopLevel(this.astFactory.createModuleDeclaration(packageName, imports, references, declarations, modifiers, uid, resourceName, definitions, kind, isLib));
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
        nativePropertyDeclaration.initializer ?
            this.astExpressionConverter.convertExpression(nativePropertyDeclaration.initializer) : null,
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

  private getStatementsFromBlock(block: ts.Block): Array<StatementDeclaration> {
    let statements: StatementDeclaration[] = [];

    for (let statement of block.statements) {
      statements.push(...this.convertStatement(statement));
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

  convertBlockStatement(block: ts.Block): StatementDeclaration {
    let statements = this.getStatementsFromBlock(block);
    return this.astFactory.createBlockStatementDeclaration(statements);
  }

  convertFunctionDeclaration(functionDeclaration: ts.FunctionDeclaration): StatementDeclaration | null {

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
        functionDeclaration.name.text,
        parameterDeclarations,
        returnType,
        typeParameterDeclarations,
        this.convertModifiers(functionDeclaration.modifiers),
        this.convertBlock(functionDeclaration.body),
        this.convertDefinitions(functionDeclaration),
        uid,
        functionDeclaration.asteriskToken
      );
    }

    return null;
  }

  convertModifiers(nativeModifiers: ts.NodeArray<ts.Modifier> | undefined): Array<ModifierDeclaration> {
    let res: Array<ModifierDeclaration> = [];
    if (nativeModifiers) {
      nativeModifiers.forEach(modifier => {
        if (modifier.kind == ts.SyntaxKind.StaticKeyword) {
          res.push(this.astFactory.createModifierDeclaration(MODIFIER_KIND.STATIC))
        } else if (modifier.kind == ts.SyntaxKind.DeclareKeyword) {
          res.push(this.astFactory.createModifierDeclaration(MODIFIER_KIND.DECLARE))
        } else if (modifier.kind == ts.SyntaxKind.ExportKeyword) {
          res.push(this.astFactory.createModifierDeclaration(MODIFIER_KIND.EXPORT))
        } else if (modifier.kind == ts.SyntaxKind.DefaultKeyword) {
          res.push(this.astFactory.createModifierDeclaration(MODIFIER_KIND.DEFAULT))
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
        declaration.asteriskToken
      );
    }

    return null;
  }


  createMethodDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: TypeDeclaration, typeParams: Array<TypeParameter>, modifiers: Array<ModifierDeclaration>, body: Block | null, isGenerator: boolean): MemberDeclaration {
    // TODO: reintroduce method declaration
    return this.astFactory.createFunctionDeclarationAsMember(name, parameters, type, typeParams, modifiers, body, "__NO_UID__", isGenerator);
  }

  createTypeDeclaration(value: string, params: Array<TypeDeclaration> = []): TypeDeclaration {
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
      return this.astFactory.createQualifiedNameEntity(
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

  private createImportSpecifier(importSpecifier: ts.ImportSpecifier): ImportSpecifierDeclaration {
    return this.astFactory.createImportSpecifier(importSpecifier.name, importSpecifier.propertyName, this.createUid(importSpecifier.name));
  }

  private createUid(identifier: ts.Identifier): string | null {
    let declarations = resolveDeclarations(identifier, this.typeChecker);

    if (declarations[0]) {
      return this.exportContext.getUID(declarations[0]);
    }

    return null;
  }

  private createTypeReferenceFromSymbol(declaration: ts.Declaration | null): ReferenceEntity | null {
    if (declaration == null) {
      return null;
    }

    let kind: ReferenceDeclarationProto.KINDMap[keyof ReferenceDeclarationProto.KINDMap] = ReferenceDeclarationProto.KIND.IRRELEVANT_KIND;
    if (ts.isClassDeclaration(declaration)) {
      kind = ReferenceDeclarationProto.KIND.CLASS
    } else if (ts.isInterfaceDeclaration(declaration)) {
      kind = ReferenceDeclarationProto.KIND.INTERFACE
    }

    let typeReference: ReferenceEntity | null = null;
    if (ts.isImportSpecifier(declaration)) {
      let uid = this.createUid(declaration.name);
      if (uid) {
        let origin = declaration.propertyName ? ReferenceDeclarationProto.ORIGIN.NAMED_IMPORT : ReferenceDeclarationProto.ORIGIN.IMPORT;
        typeReference = this.astFactory.createReferenceEntity(uid, origin, kind);
      }
    } else if (ts.isImportEqualsDeclaration(declaration)) {
      let importedSymbol = this.typeChecker.getSymbolAtLocation(declaration.name);
      if (importedSymbol) {
        let declaredTyped = this.typeChecker.getDeclaredTypeOfSymbol(importedSymbol);
        if (declaredTyped.symbol && Array.isArray(declaredTyped.symbol.declarations)) {
          typeReference = this.astFactory.createReferenceEntity(this.exportContext.getUID(declaredTyped.symbol.declarations[0]), ReferenceDeclarationProto.ORIGIN.IRRELEVANT, kind);
        }
      }
    } else {
      typeReference = this.astFactory.createReferenceEntity(this.exportContext.getUID(declaration), ReferenceDeclarationProto.ORIGIN.IRRELEVANT, kind);
    }

    return typeReference;
  }

  convertType(type: ts.TypeNode | undefined): TypeDeclaration {
    if (type == undefined) {
      return this.createTypeDeclaration("Any")
    } else {
      if (type.kind == ts.SyntaxKind.VoidKeyword) {
        return this.createTypeDeclaration("Unit")
      } else if (ts.isArrayTypeNode(type)) {
        return this.createTypeDeclaration("@@ArraySugar", [this.convertType(type.elementType)])
      } else if (ts.isUnionTypeNode(type)) {
        let params = type.types
          .map(argumentType => this.convertType(argumentType));

        return this.astFactory.createUnionTypeDeclaration(params);
      } else if (ts.isIntersectionTypeNode(type)) {
        let params = type.types
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

              typeReference = this.createTypeReferenceFromSymbol(declaration);
            }
          }
        }

        return this.astFactory.createTypeReferenceDeclarationAsParamValue(
          entity,
          params,
          typeReference
        );
      } else if (type.kind == ts.SyntaxKind.ParenthesizedType) {
        return this.convertType(type.type);
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
        let parameterDeclarations = type.parameters.map(
          (param, count) => this.convertParameterDeclaration(param, count)
        );
        return this.astFactory.createFunctionTypeDeclaration(parameterDeclarations, this.convertType(type.type))
      } else if (ts.isTypeLiteralNode(type)) {
        return this.convertTypeLiteralToObjectLiteralDeclaration(type)
      } else if (ts.isThisTypeNode(type)) {
        return this.astFactory.createThisTypeDeclaration()
      } else if (ts.isLiteralTypeNode(type)) {
        // TODO: we need to pass information on literal futher and convert it in some lowering
        let literal = type.literal;
        if ((literal.kind == ts.SyntaxKind.TrueKeyword) || (literal.kind == ts.SyntaxKind.FalseKeyword)) {
          return this.createTypeDeclaration("boolean");
        } else if (literal.kind == ts.SyntaxKind.FirstLiteralToken) {
          return this.astFactory.createNumericLiteralDeclaration(literal.getText())
        } else {
          return this.astFactory.createStringLiteralDeclaration(literal.getText());
        }
      } else if (ts.isTupleTypeNode(type)) {
        return this.astFactory.createTupleDeclaration(type.elementTypes.map(elementType => this.convertType(elementType)))
      } else if (ts.isTypePredicateNode(type)) {
        return this.createTypeDeclaration("boolean");
      } else if (type.kind == ts.SyntaxKind.ObjectKeyword) {
        return this.createTypeDeclaration("object");
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

  convertPropertySignature(node: ts.PropertySignature): MemberDeclaration | null {
    let name = this.convertName(node.name);

    if (name !== null) {
      return this.createProperty(
          name,
          node.initializer ?
              this.astExpressionConverter.convertExpression(node.initializer) : null,
          this.convertType(node.type),
          [],
          !!node.questionToken
      );
    }

    return null;
  }

  convertIndexSignature(indexSignatureDeclaration: ts.IndexSignatureDeclaration): MemberDeclaration {
    let parameterDeclarations = indexSignatureDeclaration.parameters
      .map(
        (param, count) => this.convertParameterDeclaration(param, count)
      );

      return this.astFactory.createIndexSignatureDeclaration(
        parameterDeclarations,
        this.convertType(indexSignatureDeclaration.type)
      );
  }

  convertTypeElementToInterfaceMemberDeclarations(member: ts.TypeElement): MemberDeclaration | null {
    if (ts.isMethodSignature(member)) {
      let methodDeclaration = this.convertMethodSignatureDeclaration(member);
      if (methodDeclaration) {
        return methodDeclaration
      }
    } else if (ts.isPropertySignature(member)) {
      let propertySignatureDeclaration = this.convertPropertySignature(member);
      if (propertySignatureDeclaration !== null) {
        return propertySignatureDeclaration
      }
    } else if (ts.isIndexSignatureDeclaration(member)) {
      return this.convertIndexSignature(member);
    } else if (ts.isCallSignatureDeclaration(member)) {
      return this.astFactory.createCallSignatureDeclaration(
        this.convertParameterDeclarations(member.parameters),
        member.type ? this.convertType(member.type) : this.createTypeDeclaration("Unit"),
        this.convertTypeParams(member.typeParameters)
      )
    }

    return null;
  }

  convertMembersToInterfaceMemberDeclarations(members: ts.NodeArray<ts.TypeElement>): Array<MemberDeclaration> {
    let res: Array<MemberDeclaration> = [];

    for (let member of members) {
      let memberDeclaration = this.convertTypeElementToInterfaceMemberDeclarations(member);
      if (memberDeclaration) {
        res.push(memberDeclaration);
      }
    }

    return res;
  }

  convertClassElementsToMembers(classDeclarationMembers: ts.NodeArray<ts.ClassElement> | null): Array<MemberDeclaration> {
    if (classDeclarationMembers == null) {
      return [];
    }

    let members: Array<MemberDeclaration> = [];

    for (let memberDeclaration of classDeclarationMembers) {
      if (ts.isIndexSignatureDeclaration(memberDeclaration)) {
        members.push(this.convertIndexSignature(memberDeclaration));
      } else if (ts.isPropertyDeclaration(memberDeclaration)) {
        let propertyDeclaration = this.convertPropertyDeclaration(
          memberDeclaration
        );
        if (propertyDeclaration != null) {
          members.push(propertyDeclaration)
        }
      } else if (ts.isMethodDeclaration(memberDeclaration)) {
        let convertedMethodDeclaration = this.convertMethodDeclaration(memberDeclaration);
        if (convertedMethodDeclaration != null) {
          members.push(convertedMethodDeclaration)
        }
      } else if (memberDeclaration.kind == ts.SyntaxKind.Constructor) {
        members.push(...this.convertConstructorDeclaration(memberDeclaration));
      }
    }

    return members;
  }


  convertTypeAliasWithTypeLiteralToInterfaceDeclaration(statement: ts.TypeAliasDeclaration): Declaration {
    let uid = this.exportContext.getUID(statement);
    return this.astFactory.createInterfaceDeclaration(
      this.astFactory.createIdentifierDeclarationAsNameEntity(statement.name.getText()),
      this.convertMembersToInterfaceMemberDeclarations(statement.type.members),
      this.convertTypeParams(statement.typeParameters),
      [],
      [],
      [this.astFactory.createDefinitionInfoDeclaration(uid, statement.getSourceFile().fileName)],
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
          let convertedVariable = this.convertPropertyDeclaration(parameter);
          if (convertedVariable != null) {
            res.push(convertedVariable)
          }
        }
      }

      params.push(this.convertParameterDeclaration(parameter, count));
    });

    res.push(this.astFactory.createConstructorDeclaration(
      params,
      this.convertTypeParams(constructorDeclaration.typeParameters),
      this.convertModifiers(constructorDeclaration.modifiers),
      this.convertBlock(constructorDeclaration.body)
    ));

    return res;
  }

  private convertTypeAliasDeclaration(declaration: ts.TypeAliasDeclaration): Declaration {
    return this.astFactory.createTypeAliasDeclaration(
      this.convertEntityName(declaration.name),
      this.convertTypeParams(declaration.typeParameters),
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

    return this.astFactory.createQualifiedNameEntity(convertedExpression, name);
  }

  private getFirstDeclaration(symbol: ts.Symbol | null): ts.Declaration | null {
    if (symbol == null) {
      return null;
    }

    if (Array.isArray(symbol.declarations)) {
      return symbol.declarations[0];
    }

    return null;
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
              typeArguments.push(this.convertType(typeArgument))
            }
          }

          let expression = type.expression;

          let name: NameEntity | null = null;
          if (ts.isPropertyAccessExpression(expression)) {
            name = this.convertPropertyAccessExpression(expression);
          } else if (ts.isIdentifier(expression)) {
            name = this.astFactory.createIdentifierDeclarationAsNameEntity(expression.getText());
          }

          let symbol = this.typeChecker.getSymbolAtLocation(type.expression);
          let declaration = this.getFirstDeclaration(symbol);

          // class can implement itself, but in overwhelming majority of cases this was not the intention of the declaration author - see https://stackoverflow.com/questions/62418219/class-implementing-itself-instead-of-inheriting-an-eponymous-interface-in-outer
          if (declaration != parent) {
            let typeReference = this.createTypeReferenceFromSymbol(declaration);

            if (name) {
              parentEntities.push(this.astFactory.createHeritageClauseDeclaration(
                name,
                typeArguments,
                extending,
                typeReference,
              ));
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
      this.convertDefinitions(statement),
      this.exportContext.getUID(statement)
    );
  }

  private convertDefinitions(mergeableDeclaration: ts.FunctionDeclaration | ts.InterfaceDeclaration | ts.ClassDeclaration | ts.VariableDeclaration | ts.ModuleDeclaration): Array<DefinitionInfoDeclaration> {
    return this.declarationResolver.resolve(mergeableDeclaration).map((definitionInfo) => {
      return this.astFactory.createDefinitionInfoDeclaration(this.exportContext.getUID(definitionInfo), definitionInfo.getSourceFile().fileName);
    });
  }

  convertInterfaceDeclaration(statement: ts.InterfaceDeclaration): Declaration {
    return this.astFactory.createInterfaceDeclaration(
      this.astFactory.createIdentifierDeclarationAsNameEntity(statement.name.getText()),
      this.convertMembersToInterfaceMemberDeclarations(statement.members),
      this.convertTypeParams(statement.typeParameters),
      this.convertHeritageClauses(statement.heritageClauses, statement),
      this.convertModifiers(statement.modifiers),
      this.convertDefinitions(statement),
      this.exportContext.getUID(statement)
    );
  }

  convertBindingElements(elements: ts.NodeArray<ts.ArrayBindingElement>): Array<BindingElementDeclaration> {
    let res: Array<BindingElementDeclaration> = [];

    for (let element of elements) {

      if (ts.isIdentifier(element.name)) {
        res.push(this.astFactory.createBindingVariableDeclaration(
            element.name.getText(),
            element.initializer == null ? null : this.astExpressionConverter.convertExpression(element.initializer),
        ));
      } else if (ts.isArrayBindingPattern(element.name)) {
        res.push(this.astFactory.declareArrayBindingPatternAsBindingElement(
            this.convertBindingElements(element.name.elements)
        ))
      }
    }

    return res
  }

  convertVariableDeclarationList(list: ts.VariableDeclarationList, modifiers: ts.ModifiersArray | null): Array<StatementDeclaration> {
    let res: Array<StatementDeclaration> = [];

    for (let declaration of list.declarations) {

      if (ts.isIdentifier(declaration.name)) {
        res.push(this.astFactory.declareVariable(
            declaration.name.getText(),
            this.convertType(declaration.type),
            this.convertModifiers(modifiers),
            declaration.initializer == null ? null : this.astExpressionConverter.convertExpression(declaration.initializer),
            this.convertDefinitions(declaration),
            this.exportContext.getUID(declaration)
        ));
      } else if (ts.isArrayBindingPattern(declaration.name)) {
        res.push(this.astFactory.declareArrayBindingPatternAsStatement(
            this.convertBindingElements(declaration.name.elements)
        ))
      }
    }

    return res
  }

  convertIterationStatement(statement: ts.Node): StatementDeclaration | null {
    let decl: StatementDeclaration | null = null;

    let body = this.convertStatement(statement.statement);

    if (ts.isWhileStatement(statement)) {
      decl = this.astFactory.createWhileStatement(
        this.astExpressionConverter.convertExpression(statement.expression),
        body
      )
    }

    if (ts.isForStatement(statement)) {

      decl = this.astFactory.createForStatement(
        this.convertVariableDeclarationList(statement.initializer, null),
        statement.condition ? this.astExpressionConverter.convertExpression(statement.condition) : null,
        statement.incrementor ? this.astExpressionConverter.convertExpression(statement.incrementor) : null,
        body
      )
    }

    if (ts.isForOfStatement(statement)) {
      decl = this.astFactory.createForOfStatement(
          this.convertVariableDeclarationList(statement.initializer, null),
          this.astExpressionConverter.convertExpression(statement.expression),
          body
      )
    }

    //TODO convert other iteration statements than while statement

    return decl
  }

  convertSwitchStatement(statement: ts.SwitchStatement): StatementDeclaration {
    let expression = this.astExpressionConverter.convertExpression(statement.expression);
    let cases: Array<CaseDeclarationProto> = [];
    for (let clause of statement.caseBlock.clauses) {
      let body: Array<StatementDeclaration> = [];
      for (let statement of clause.statements) {
        body.push(...this.convertStatement(statement));
      }
      cases.push(this.astFactory.createCaseDeclaration(
          ts.isCaseClause(clause) ?
              this.astExpressionConverter.convertExpression(clause.expression) : null,
          body
      ));
    }

    return this.astFactory.createSwitchStatement(
        expression, cases
    )
  }

  convertStatement(statement: ts.Node): Array<StatementDeclaration> {
    if (ts.isExpressionStatement(statement)) {
      return [this.astFactory.createExpressionStatement(
        this.astExpressionConverter.convertExpression(statement.expression)
      )]
    } else if (ts.isIfStatement(statement)) {
      return [this.astFactory.createIfStatement(
        this.astExpressionConverter.convertExpression(statement.expression),
        this.convertStatement(statement.thenStatement),
        statement.elseStatement ? this.convertStatement(statement.elseStatement) : null
      )]
    } else if (ts.isIterationStatement(statement)) {
      let iterationStatement = this.convertIterationStatement(statement);

      if (iterationStatement) {
        return [iterationStatement]
      }
    } else if (ts.isReturnStatement(statement)) {
      return [this.astFactory.createReturnStatement(
        statement.expression ? this.astExpressionConverter.convertExpression(statement.expression) : null
      )]
    } else if (ts.isBreakStatement(statement)) {
      return [this.astFactory.createBreakStatement()]
    } else if (ts.isContinueStatement(statement)) {
      return [this.astFactory.createContinueStatement()]
    } else if (ts.isThrowStatement(statement)) {
      return [this.astFactory.createThrowStatement(
        this.astExpressionConverter.convertExpression(statement.expression)
      )]
    } else if (ts.isVariableStatement(statement)) {
      return this.convertVariableDeclarationList(statement.declarationList, statement.modifiers)
    } else if (ts.isFunctionDeclaration(statement)) {
      let convertedFunctionDeclaration = this.convertFunctionDeclaration(statement);
      if (convertedFunctionDeclaration != null) {
        return [convertedFunctionDeclaration];
      }
    } else if (ts.isBlock(statement)) {
      let block = this.convertBlockStatement(statement);
      if (block) {
        return [block]
      }
    } else if (ts.isSwitchStatement(statement)) {
      let switchStatement = this.convertSwitchStatement(statement);
      return [switchStatement];
    }

    return [];
  }

  * convertTopLevelStatement(statement: ts.Node): Iterable<TopLevelDeclarationProto> {
    if (ts.isEnumDeclaration(statement)) {
      let enumTokens = statement.members.map(member =>
        this.astFactory.createEnumTokenDeclaration(
          member.name.getText(),
          member.initializer ? member.initializer.getText() : ""
        ));

      yield this.astFactory.createEnumDeclaration(
        statement.name.getText(),
        enumTokens,
        this.exportContext.getUID(statement)
      )
    } else if (
      ts.isExpressionStatement(statement) ||
      ts.isIfStatement(statement) ||
      ts.isIterationStatement(statement) ||
      ts.isReturnStatement(statement) ||
      ts.isThrowStatement(statement) ||
      ts.isBlock(statement) ||
      ts.isVariableStatement(statement) ||
      ts.isFunctionDeclaration(statement)
    ) {
      yield this.astFactory.createStatementAsTopLevel(
        this.convertStatement(statement)[0]
      )
    } else if (ts.isTypeAliasDeclaration(statement)) {
      if (ts.isTypeLiteralNode(statement.type)) {
        yield this.convertTypeAliasWithTypeLiteralToInterfaceDeclaration(
          statement
        )
      } else {
        yield this.convertTypeAliasDeclaration(statement)
      }
    } else if (ts.isClassDeclaration(statement)) {
      let classDeclaration = this.convertClassDeclaration(statement);

      if (classDeclaration != null) {
        yield classDeclaration;
      }
    } else if (ts.isInterfaceDeclaration(statement)) {
      yield this.convertInterfaceDeclaration(statement);
    } else if (ts.isExportAssignment(statement)) {
      let expression = statement.expression;
      if (ts.isIdentifier(expression) || ts.isPropertyAccessExpression(expression)) {
        let symbol = this.typeChecker.getSymbolAtLocation(expression);

        if (symbol) {

          if (symbol.flags & ts.SymbolFlags.Alias) {
            symbol = this.typeChecker.getAliasedSymbol(symbol);
          }

          if (Array.isArray(symbol.declarations) && symbol.declarations.length > 0) {
            yield this.astFactory.createExportAssignmentDeclaration(
              symbol.declarations.map(it => this.exportContext.getUID(it)),
              !!statement.isExportEquals
            )
          }
        }
      } else {
        this.log.info(`skipping unknown expression assignment: [${expression.kind}]`);
      }
    } else if (ts.isImportEqualsDeclaration(statement)) {
      if (ts.isEntityName(statement.moduleReference)) {
        let moduleReferenceDeclaration = this.convertEntityName(statement.moduleReference);
        let uid = ts.isModuleBlock(statement.parent) ? this.exportContext.getUID(statement.parent.parent) : this.exportContext.getUID(statement.parent);

        yield this.astFactory.createImportEqualsDeclaration(
          statement.name.getText(),
          moduleReferenceDeclaration,
          uid
        );
      } else {
        this.log.info(`skipping external module reference ${statement.moduleReference.getText()}, kind: ${statement.moduleReference.kind}`)
      }
    } else if (ts.isModuleDeclaration(statement)) {
      let module = this.convertModule(statement);
      if (module) {
        yield module
      }
    } else {
      this.unsupportedDeclarations.add(statement.kind);
    }
  }

  private * convertStatements(statements: ts.NodeArray<ts.Node>): Iterable<Declaration> {
    for (let statement of statements) {
      yield* this.convertTopLevelStatement(statement)
    }
  }

  private resolveAmbientModuleName(moduleDeclaration: ts.ModuleDeclaration): string {
    if (ts.isNonGlobalAmbientModule(moduleDeclaration) && ts.isExternalModuleAugmentation(moduleDeclaration)) {
      let moduleSymbol = this.typeChecker.getSymbolAtLocation(moduleDeclaration.name);

      if (moduleSymbol && Array.isArray(moduleSymbol.declarations)) {
        let firstDeclaration = moduleSymbol.declarations[0];
        if (firstDeclaration && firstDeclaration.name) {
          return firstDeclaration.name.getText() || moduleDeclaration.name.getText();
        }
      }
    }

    return moduleDeclaration.name.getText();
  }

  private convertModuleBody(body: ts.ModuleBody | null, filter?: (node: ts.Node) => boolean): TopLevelDeclarationProto | null {
    let declarations: Iterable<Declaration> | undefined;

    if (ts.isModuleBlock(body)) {
      let statements = filter ? body.statements.filter(filter) : body.statements;
      declarations = this.convertStatements(statements);
    } else if (ts.isModuleDeclaration(body)) {
      let convertedModule = this.convertModule(body, filter);
      if (convertedModule) {
        declarations = [convertedModule];
      }
    }

    if (declarations) {
      let parentModule = body.parent;

      let modifiers = this.convertModifiers(parentModule.modifiers);
      let uid = this.exportContext.getUID(parentModule);
      let sourceNameFragment = this.resolveAmbientModuleName(parentModule);

      let packageName = this.astFactory.createIdentifierDeclarationAsNameEntity(sourceNameFragment);
      let imports = this.getImports(body.getSourceFile());
      let references = this.getReferences(body.getSourceFile());

      let isLib = this.isLibNode(body)
      return this.createModuleDeclarationAsTopLevel(packageName, imports, references, declarations, modifiers, uid, sourceNameFragment, this.convertDefinitions(parentModule), (parentModule.flags & ts.NodeFlags.Namespace) ? MODULE_KIND.NAMESPACE : MODULE_KIND.AMBIENT_MODULE, isLib);
    }

    return null;
  }

  private convertModule(module: ts.ModuleDeclaration, filter?: (node: ts.Node) => boolean): TopLevelDeclarationProto | null {
    return this.convertModuleBody(module.body, filter);
  }
}