declare function uid(): string;
declare function createExportContent(): ExportContext;
declare function createAstFactory(): AstFactory;
declare function createFileResolver(): FileResolver;

// Declarations that declared inside namespace marked as internal and not exist inside typescriptServices.d.ts and typescript.d.ts, but available at runtime
declare namespace ts {
    function normalizePath(path: string): string;
    function getDirectoryPath(path: string): string;
    var libMap : { get(path: string) : string };
}


class AstConverter {
    private exportContext = createExportContent();
    private log = createLogger("AstConverter");

    private resources = new ResourceFetcher(this.sourceFileFetcher).build(this.sourceName);

    private libVisitor = new LibraryDeclarationsVisitor(
      this.resources,
      this.typeChecker,
      this
    );

    constructor(
        private sourceName: string,
        private rootPackageName: NameDeclaration,
        private typeChecker: ts.TypeChecker,
        private sourceFileFetcher: (fileName: string) => ts.SourceFile | undefined,
        private declarationResolver: (node: ts.Node, fileName: string) => readonly ts.DefinitionInfo[] | undefined,
        private astFactory: AstFactory
    ) {
    }

    private registerDeclaration(declaration: Declaration, collection: Array<Declaration>) {
        collection.push(declaration);
    }

    createSourceFileDeclaration(sourceFileName: string): SourceFileDeclaration {
        const sourceFile = this.sourceFileFetcher(sourceFileName);

        if (sourceFile == null) {
            throw new Error(`failed to resolve ${sourceFileName}`)
        }

        this.libVisitor.visit(sourceFile);

        let resourceName = this.rootPackageName;

        let packageNameFragments = sourceFile.fileName.split("/");
        let sourceName = packageNameFragments[packageNameFragments.length - 1].replace(".d.ts", "");

        const declarations = this.convertStatements(sourceFile.statements, resourceName);

        let packageDeclaration = this.createDocumentRoot(resourceName, declarations, this.convertModifiers(sourceFile.modifiers), [], uid(), sourceName, true);
        return this.astFactory.createSourceFileDeclaration(
            sourceFileName,
            packageDeclaration,
            sourceFile.referencedFiles.map(referencedFile => this.astFactory.createIdentifierDeclaration(referencedFile.fileName))
        );
    }

    convertSourceSet(sourceSet: Array<SourceFileDeclaration>): SourceSet {
        let sources: Array<SourceFileDeclaration> = [];

        sourceSet.forEach( source => {
            sources.push(source);
        });

        this.libVisitor.forEachLibDeclaration((libDeclarations, resourceName) => {
            sources.push(this.astFactory.createSourceFileDeclaration(
              "<LIBROOT>", this.astFactory.createDocumentRoot(
                this.astFactory.createIdentifierDeclaration("<LIBROOT>"),
                libDeclarations,
                [],
                [],
                `<LIBROOT-${resourceName}>`,
                resourceName,
                true
              ), []
            ));
        });

        return this.astFactory.createSourceSet(sources);
    }

    createSourceSet(): SourceSet {
        let sources: Array<SourceFileDeclaration> = [];
        this.resources.forEach(resource => {
            sources.push(this.createSourceFileDeclaration(resource))
        });

        return this.convertSourceSet(sources);
    }

    createDocumentRoot(packageName: NameDeclaration, declarations: Declaration[], modifiers: Array<ModifierDeclaration>, definitionsInfo: Array<DefinitionInfoDeclaration>, uid: string, resourceName: string, root: boolean): PackageDeclaration {
        return this.astFactory.createDocumentRoot(packageName, declarations, modifiers, definitionsInfo, uid, resourceName, root);
    }

    convertName(name: ts.BindingName | ts.PropertyName) : string | null {
        //TODO: this should be process at frontend
        if (ts.isNumericLiteral(name)) {
            return "`" + name.getText() + "`";
        } else if (ts.isStringLiteral(name)) {
            return "`" + name.getText().replace(/^["']/, "").replace(/["']$/,"") + "`";
        } else if (ts.isIdentifier(name)) {
            return name.getText();
        }
        return null
    }

    convertPropertyDeclaration(nativePropertyDeclaration: (ts.PropertyDeclaration | ts.ParameterDeclaration)) : PropertyDeclaration | null {
        let name = this.convertName(nativePropertyDeclaration.name);

        if (name != null) {
            return this.astFactory.declareProperty(
                name,
                this.convertType(nativePropertyDeclaration.type),
                [],
                false,
                this.convertModifiers(nativePropertyDeclaration.modifiers)
            );
        }

        return null;
    }

    convertTypeParams(nativeTypeDeclarations: ts.NodeArray<ts.TypeParameterDeclaration> | undefined) : Array<TypeParameter> {
        let typeParameterDeclarations: Array<TypeParameter> = [];

        if (nativeTypeDeclarations) {
            typeParameterDeclarations = nativeTypeDeclarations.map(typeParam => {
                const constraint = typeParam.constraint;
                return this.astFactory.createTypeParam(this.astFactory.createIdentifierDeclaration(typeParam.name.getText()), constraint ? [
                    this.convertType(constraint)
                ] : [])
            });
        }

        return typeParameterDeclarations;
    }

    convertTypeParamsToTokens(nativeTypeDeclarations: ts.NodeArray<ts.TypeParameterDeclaration> | undefined) : Array<IdentifierDeclaration> {
        let typeParameterDeclarations: Array<IdentifierDeclaration> = [];

        if (nativeTypeDeclarations) {
            typeParameterDeclarations = nativeTypeDeclarations.map(typeParam => {
                return this.astFactory.createIdentifierDeclaration(typeParam.name.getText())
            });
        }

        return typeParameterDeclarations;
    }

    convertFunctionDeclaration(functionDeclaration: ts.FunctionDeclaration) : FunctionDeclaration | null  {

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

            return this.astFactory.createFunctionDeclaration(
                functionDeclaration.name ? functionDeclaration.name.getText() : "",
                parameterDeclarations,
                returnType,
                typeParameterDeclarations,
                this.convertModifiers(functionDeclaration.modifiers),
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


    convertMethodSignatureDeclaration(declaration: ts.MethodSignature) : MethodSignatureDeclaration | null  {
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


    convertMethodDeclaration(declaration: ts.MethodSignature) : FunctionDeclaration | null  {
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
                this.convertModifiers(declaration.modifiers)
            );
        }

        return null;
    }


    createMethodDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParams: Array<TypeParameter>, modifiers: Array<ModifierDeclaration>): FunctionDeclaration {
        // TODO: reintroduce method declaration
        return this.astFactory.createFunctionDeclaration(name, parameters, type, typeParams, modifiers, "__NO_UID__");
    }

    private createTypeDeclaration(value: string, params: Array<ParameterValue> = []): TypeDeclaration {
        return this.astFactory.createTypeDeclaration(this.astFactory.createIdentifierDeclaration(value), params);
    }

    createParameterDeclaration(name: string, type: ParameterValue, initializer: Expression | null, vararg: boolean, optional: boolean): ParameterDeclaration {
        return this.astFactory.createParameterDeclaration(name, type, initializer, vararg, optional);
    }

    private createProperty(value: string, type: ParameterValue, typeParams: Array<TypeParameter> = [], optional: boolean): PropertyDeclaration {
        return this.astFactory.declareProperty(value, type, typeParams, optional, []);
    }


    createIntersectionType(params: Array<TypeDeclaration>) {
        return this.astFactory.createIntersectionTypeDeclaration(params);
    }

    convertEntityName(entityName: ts.EntityName) : NameDeclaration | null {
        if (ts.isIdentifier(entityName)) {
            return this.astFactory.createIdentifierDeclaration(entityName.text)
        } else if (ts.isQualifiedName(entityName)) {
            return this.astFactory.createQualifiedNameDeclaration(
                this.convertEntityName(entityName.left) as ModuleReferenceDeclaration,
                this.convertEntityName(entityName.right) as ModuleReferenceDeclaration
            )
        }

        return null
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

    convertType(type: ts.TypeNode | ts.Identifier | undefined) : ParameterValue {
        if (type == undefined) {
            return this.createTypeDeclaration("Any")
        } else {
            if (ts.isIdentifier(type)) {
              return this.astFactory.createIdentifierDeclaration(type.text)
            } if (type.kind == ts.SyntaxKind.VoidKeyword) {
                return this.createTypeDeclaration("Unit")
            } else if (ts.isArrayTypeNode(type)) {
                let arrayType = type as ts.ArrayTypeNode;
                return this.createTypeDeclaration("@@ArraySugar", [
                    this.convertType(arrayType.elementType)
                ] as Array<TypeDeclaration>)
            } else if (ts.isUnionTypeNode(type)) {
                let unionTypeNode = type as ts.UnionTypeNode;
                let params = unionTypeNode.types
                    .map(argumentType => this.convertType(argumentType)) as Array<TypeDeclaration>;

                return this.astFactory.createUnionTypeDeclaration(params)
            } else if (ts.isIntersectionTypeNode(type)) {
                let intersectionTypeNode = type as ts.IntersectionTypeNode;
                let params = intersectionTypeNode.types
                    .map(argumentType => this.convertType(argumentType)) as Array<TypeDeclaration>;

                return this.createIntersectionType(params);
            } else if (ts.isTypeReferenceNode(type)) {
                let params = this.convertTypeArguments(type.typeArguments);

                if (ts.isQualifiedName(type.typeName)) {
                    let entity = this.convertEntityName(type.typeName);
                    if (entity) {
                        return this.astFactory.createTypeDeclaration(entity,  params);
                    }
                }

                return this.createTypeDeclaration(
                    type.typeName.getText(),
                    params
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
            } else if (ts.isTypeLiteralNode(type))  {
                return this.convertTypeLiteralToObjectLiteralDeclaration(type as ts.TypeLiteralNode)
            } else if (ts.isThisTypeNode(type)){
                return this.astFactory.createThisTypeDeclaration()
            } else if (ts.isLiteralTypeNode(type)) {
                return this.astFactory.createStringLiteralDeclaration(type.literal.getText())
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
                this.log.debug(`unsupported declaration type: [${type.kind}]`);
                return this.createTypeDeclaration("any");
            }
        }
    }

    convertParameterDeclarations(parameters: ts.NodeArray<ts.ParameterDeclaration>) : Array<ParameterDeclaration> {
        return parameters.map((parameter, count) => this.convertParameterDeclaration(parameter, count));
    }

    convertParameterDeclaration(param: ts.ParameterDeclaration, index: number) : ParameterDeclaration {
        let initializer = null;
        if (param.initializer != null) {
            // TODO: this never happens in tests and I should add one
            initializer = this.astFactory.createExpression(
                this.createTypeDeclaration("definedExternally"),
                param.initializer.getText()
            )
        } else if (param.questionToken != null) {
            initializer = this.astFactory.createExpression(
                this.createTypeDeclaration("definedExternally"),
                "null"
            )
        }

        let paramType = this.convertType(param.type);

        let name = ts.isIdentifier(param.name) ? param.name.getText() : `__${index}`;

        return this.createParameterDeclaration(
            name,
            paramType,
            initializer,
            !!param.dotDotDotToken,
            !!param.questionToken
        )
    }

    convertTypeElementToMethodSignatureDeclaration(methodDeclaration: ts.MethodSignature) : MethodSignatureDeclaration | null {
        let convertedMethodDeclaration = this.convertMethodSignatureDeclaration(methodDeclaration);
        if (convertedMethodDeclaration != null) {
            return convertedMethodDeclaration
        }

        return null;
    }

    convertTypeElementToMemberDeclaration(methodDeclaration: ts.MethodSignature) : MemberDeclaration | null {
        let convertedMethodDeclaration = this.convertMethodDeclaration(methodDeclaration);
        if (convertedMethodDeclaration != null) {
            return convertedMethodDeclaration
        }

        return null;
    }

    convertPropertySignature(node: ts.PropertySignature) : MemberDeclaration | null  {
        let name = this.convertName(node.name);

        if (name !== null) {
            return this.createProperty(name, this.convertType(node.type), [], !!node.questionToken);
        }

        return null;
    }

    convertIndexSignature(indexSignatureDeclaration: ts.IndexSignatureDeclaration) : Array<MemberDeclaration> {
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

    convertTypeElementToInterfaceMemberDeclarations(member: ts.TypeElement) : Array<MemberDeclaration> {
        let  res: Array<MemberDeclaration> = [];
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

    convertMembersToInterfaceMemberDeclarations(members: ts.NodeArray<ts.TypeElement>) : Array<MemberDeclaration> {
        let res: Array<MemberDeclaration> = [];
        members.map(member => {
            this.convertTypeElementToInterfaceMemberDeclarations(member).map(memberDeclaration => {
                this.registerDeclaration(memberDeclaration, res);
            });
        });

        return res;
    }

    convertClassElementsToMembers(classDeclarationMembers: ts.NodeArray<ts.ClassElement> | null) : Array<MemberDeclaration> {
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


    convertTypeLiteralToInterfaceDeclaration(name: string, typeLiteral: ts.TypeLiteralNode, typeParams: ts.NodeArray<ts.TypeParameterDeclaration> | undefined): InterfaceDeclaration {
        return this.astFactory.createInterfaceDeclaration(
            name,
            this.convertMembersToInterfaceMemberDeclarations(typeLiteral.members),
            this.convertTypeParams(typeParams),
            [],
            [],
            this.exportContext.getUID(typeLiteral)
        );
    }

    convertTypeLiteralToObjectLiteralDeclaration(typeLiteral: ts.TypeLiteralNode): ObjectLiteral {
        return this.astFactory.createObjectLiteral(
            this.convertMembersToInterfaceMemberDeclarations(typeLiteral.members)
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

        this.registerDeclaration(this.astFactory.createConstructorDeclaration(
            params,
            this.createTypeDeclaration("______"),
            this.convertTypeParams(constructorDeclaration.typeParameters), this.convertModifiers(constructorDeclaration.modifiers)),
            res
        );

        return res;
    }

    private convertTypeAliasDeclaration(declaration: ts.TypeAliasDeclaration): TypeAliasDeclaration {

        return this.astFactory.createTypeAliasDeclaration(
            this.convertType(declaration.name),
            this.convertTypeParamsToTokens(declaration.typeParameters),
            this.convertType(declaration.type),
            declaration.name.getText() + "_TYPE"
        )
    }

    private convertPropertyAccessExpression(propertyAccessExpression: ts.PropertyAccessExpression): QualifierDeclaration  {
        let convertedExpression: NameDeclaration | null;
        let name = this.astFactory.createIdentifierDeclaration(propertyAccessExpression.name.text);

        if (ts.isIdentifier(propertyAccessExpression.expression)) {
            convertedExpression = this.astFactory.createIdentifierDeclaration(propertyAccessExpression.expression.text);
        } else if (ts.isPropertyAccessExpression(propertyAccessExpression.expression)) {
            convertedExpression = this.convertPropertyAccessExpression(propertyAccessExpression.expression);
        } else {
            // TODO: we can not have errors to be honest
            throw new Error("never supposed to be there")
        }

        return this.astFactory.createQualifiedNameDeclaration(convertedExpression, name);
    }

    private convertValue(entity: ts.TypeNode | ts.Identifier): NameDeclaration {
        let convertedEntity = this.convertType(entity) as any;

        // TODO: getValue() we get in Graal, .value in Nashorn and J2V8 - I need to create a minimal example and report it
        let value = typeof convertedEntity.getValue == "function" ?
            (convertedEntity).getValue() : convertedEntity.value;

        return value as NameDeclaration;
    }

    convertHeritageClauses(heritageClauses: ts.NodeArray<ts.HeritageClause> | undefined): Array<HeritageClauseDeclaration> {
        let parentEntities: Array<HeritageClauseDeclaration> = [];


            if (heritageClauses) {
                for (let heritageClause of heritageClauses) {

                    let extending = heritageClause.token == ts.SyntaxKind.ExtendsKeyword;

                    for (let type of heritageClause.types) {
                        let typeArguments: Array<IdentifierDeclaration> = [];

                        if (type.typeArguments) {
                            for (let typeArgument of type.typeArguments) {
                                this.registerDeclaration(this.convertType(typeArgument), typeArguments)
                            }
                        }

                        let expression = type.expression;

                        if (ts.isPropertyAccessExpression(expression)) {
                            let name = this.convertPropertyAccessExpression(expression);

                            this.registerDeclaration(
                                this.astFactory.createHeritageClauseDeclaration(
                                    name,
                                    typeArguments,
                                    extending
                                ), parentEntities
                            );
                        } else if (ts.isIdentifier(expression)) {
                            let name = this.astFactory.createIdentifierDeclaration(expression.getText());

                            this.registerDeclaration(
                                this.astFactory.createHeritageClauseDeclaration(
                                    name,
                                    typeArguments,
                                    extending
                                ), parentEntities
                            );
                        }


                    }
                }
            }

        return  parentEntities
    }

    convertClassDeclaration(statement: ts.ClassDeclaration): ClassDeclaration | null {
        if (statement.name == undefined) {
            return null;
        }

        let classDeclaration = this.astFactory.createClassDeclaration(
          statement.name.getText(),
          this.convertClassElementsToMembers(statement.members),
          this.convertTypeParams(statement.typeParameters),
          this.convertHeritageClauses(statement.heritageClauses),
          this.convertModifiers(statement.modifiers),
          this.exportContext.getUID(statement)
        );

        return classDeclaration;
    }

    private convertDefinitions(name: ts.Node): Array<DefinitionInfoDeclaration> {
        let definitionInfos = this.declarationResolver(name, this.sourceName);

        var definitionsInfoDeclarations: Array<DefinitionInfoDeclaration> = [];
        if (definitionInfos) {
            definitionsInfoDeclarations = definitionInfos.map(definitionInfo => {
                return this.astFactory.createDefinitionInfoDeclaration(definitionInfo.fileName);
            });
        }

        return definitionsInfoDeclarations;
    }

    convertInterfaceDeclaration(statement: ts.InterfaceDeclaration, computeDefinitions: boolean = true): InterfaceDeclaration  {

        let interfaceDeclaration = this.astFactory.createInterfaceDeclaration(
          statement.name.getText(),
          this.convertMembersToInterfaceMemberDeclarations(statement.members),
          this.convertTypeParams(statement.typeParameters),
          this.convertHeritageClauses(statement.heritageClauses),
          computeDefinitions? this.convertDefinitions(statement.name) : [],
          this.exportContext.getUID(statement)
        );

        return interfaceDeclaration;
    }

    private convertStatement(statement: ts.Node, resourceName: NameDeclaration): Array<Declaration> {
        let res: Array<Declaration> = [];

        if (ts.isEnumDeclaration(statement)) {
            let enumTokens = statement.members.map(member =>
                this.astFactory.createEnumTokenDeclaration(
                    member.name.getText(),
                    member.initializer ? member.initializer.getText() : ""
                ));

            res.push(this.astFactory.createEnumDeclaration(
                statement.name.getText(),
                enumTokens
            ))
        } else if (ts.isVariableStatement(statement)) {
            for (let declaration of statement.declarationList.declarations) {
                res.push(this.astFactory.declareVariable(
                    declaration.name.getText(),
                    this.convertType(declaration.type),
                    this.convertModifiers(statement.modifiers),
                    this.exportContext.getUID(declaration)
                ));
            }
        } else if (ts.isTypeAliasDeclaration(statement)) {
            if (ts.isTypeLiteralNode(statement.type)) {
                res.push(this.convertTypeLiteralToInterfaceDeclaration(
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
            let definitionInfos = this.declarationResolver(statement.name, this.sourceName);

            var definitionsInfoDeclarations: Array<DefinitionInfoDeclaration> = [];
            if (definitionInfos) {
                definitionsInfoDeclarations = definitionInfos.map(definitionInfo => {
                    return this.astFactory.createDefinitionInfoDeclaration(definitionInfo.fileName);
                });
            }

            res.push(this.convertInterfaceDeclaration(statement));
        } else if (ts.isModuleDeclaration(statement)) {
            for (let moduleDeclaration of this.convertModule(statement, resourceName)) {
                res.push(moduleDeclaration);
            }
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
            }  else {
                this.log.info(`skipping unknown expression assignment: [${expression.kind}]`);
            }
        } else if (ts.isImportEqualsDeclaration(statement)) {

            if (ts.isEntityName(statement.moduleReference)) {
                let moduleReferenceDeclaration = this.convertEntityName(statement.moduleReference);
                let uid = ts.isModuleBlock(statement.parent) ? this.exportContext.getUID(statement.parent.parent) : this.exportContext.getUID(statement.parent);

                res.push(this.astFactory.createImportEqualsDeclaration(
                    statement.name.getText(),
                    moduleReferenceDeclaration as ModuleReferenceDeclaration,
                    uid
                ));
            } else {
                this.log.info(`skipping external module reference ${statement.moduleReference.getText()}, kind: ${statement.moduleReference.kind}`)
            }
        } else {
            this.log.info(`skipping ${statement.kind}`);
        }

        return res;
    }

    private convertStatements(statements: ts.NodeArray<ts.Node>, resourceName: NameDeclaration) : Array<Declaration> {
        const declarations: Declaration[] = [];
        for (let statement of statements) {
            for (let decl of this.convertStatement(statement, resourceName)) {
                this.registerDeclaration(decl, declarations)
            }
        }

        return declarations;
    }

    convertModule(module: ts.ModuleDeclaration, resourceName: NameDeclaration): Array<Declaration> {
        let definitionInfos = this.declarationResolver(module.name, this.sourceName);

        const declarations: Declaration[] = [];
        if (module.body) {
            var definitionsInfoDeclarations: Array<DefinitionInfoDeclaration> = [];
            if (definitionInfos) {
                definitionsInfoDeclarations = definitionInfos.map(definitionInfo => {
                    return this.astFactory.createDefinitionInfoDeclaration(definitionInfo.fileName);
                });
            }

            let body = module.body;
            let modifiers = this.convertModifiers(module.modifiers);
            let uid = this.exportContext.getUID(module);
            let sourceNameFragment = module.name.getText();

            if (ts.isModuleBlock(body)) {
                let moduleDeclarations = this.convertStatements(body.statements, resourceName);
                this.registerDeclaration(this.createDocumentRoot(this.astFactory.createIdentifierDeclaration(sourceNameFragment), moduleDeclarations, modifiers, definitionsInfoDeclarations, uid, sourceNameFragment, false), declarations);
            } else if (ts.isModuleDeclaration(body)) {
                this.registerDeclaration(this.createDocumentRoot(this.astFactory.createIdentifierDeclaration(sourceNameFragment), this.convertModule(body, resourceName), modifiers, definitionsInfoDeclarations, uid, sourceNameFragment, false), declarations);
            }
        }
        return declarations
    }

}