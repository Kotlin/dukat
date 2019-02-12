class AstConverter {

    private exportContext = new ExportContext();

    constructor(private astFactory: AstFactory, private typeChecker: ts.TypeChecker) {
    }

    private registerDeclaration(declaration: Declaration, collection: Array<Declaration>) {
        collection.push(declaration);
    }

    createDocumentRoot(packageName: string, declarations: Declaration[], modifiers: Array<ModifierDeclaration>, uid: string): DocumentRoot {
        return this.astFactory.createDocumentRoot(packageName, declarations, modifiers, uid);
    }

    convertName(name: ts.BindingName | ts.PropertyName) : string | null {
        if (ts.isNumericLiteral(name)) {
            return "`" + name.getText() + "`";
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
                return this.astFactory.createTypeParam(typeParam.name.getText(), constraint ? [
                    this.convertType(constraint)
                ] : [])
            });
        }

        return typeParameterDeclarations;
    }

    convertTypeParamsToTokens(nativeTypeDeclarations: ts.NodeArray<ts.TypeParameterDeclaration> | undefined) : Array<TokenDeclaration> {
        let typeParameterDeclarations: Array<TokenDeclaration> = [];

        if (nativeTypeDeclarations) {
            typeParameterDeclarations = nativeTypeDeclarations.map(typeParam => {
                return this.astFactory.createTokenDeclaration(typeParam.name.getText())
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
        return this.astFactory.createTypeDeclaration(value, params);
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

    convertEntityName(entityName: ts.EntityName) : ModuleReferenceDeclaration | null {
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

    convertType(type: ts.TypeNode | ts.TypeElement | ts.EntityName | undefined) : ParameterValue {
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
            } else {
                if (ts.isUnionTypeNode(type)) {
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
                    if (type.typeArguments) {
                        let params = type.typeArguments
                            .map(argumentType => {
                                return this.convertType(argumentType)
                            }) as Array<TypeDeclaration>;

                        return this.createTypeDeclaration(type.typeName.getText(), params)
                    } else {
                        if (ts.isQualifiedName(type.typeName)) {
                            let entity = this.convertEntityName(type.typeName);
                            if (entity) {
                                return entity
                            }
                        }
                        return this.createTypeDeclaration(type.typeName.getText())
                    }
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
                    return this.astFactory.createStringTypeDeclaration([
                        type.literal.getText()
                    ])
                } else {
                    return this.createTypeDeclaration(`__UNKNOWN__:${type.kind}`)
                }
            }
        }
    }

    convertParameterDeclarations(parameters: ts.NodeArray<ts.ParameterDeclaration>) : Array<ParameterDeclaration> {
        return parameters.map((parameter, count) => this.convertParameterDeclaration(parameter, count));
    }

    convertParameterDeclaration(param: ts.ParameterDeclaration, index: number) : ParameterDeclaration {
        let initializer = null;
        if (param.initializer != null) {
            initializer = this.astFactory.createExpression(
                this.createTypeDeclaration("@@DEFINED_EXTERNALLY"),
                param.initializer.getText()
            )
        } else if (param.questionToken != null) {
            initializer = this.astFactory.createExpression(
                this.createTypeDeclaration("@@DEFINED_EXTERNALLY"),
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

    convertPropertySignature(node: ts.PropertySignature) : MemberDeclaration  {
         return this.createProperty(this.convertName(node.name) as string, this.convertType(node.type), [], !!node.questionToken);
    }

    convertIndexSignature(indexSignatureDeclaration: ts.IndexSignatureDeclaration) : Array<MemberDeclaration> {
        let res: Array<MemberDeclaration> = [];
        //let typeParameterDeclarations: Array<TypeParameter> = this.convertTypeParams(indexSignatureDeclaration.typeParameters);
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
            this.registerDeclaration(
                this.convertPropertySignature(member), res
            );
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
            declaration.name.getText(),
            this.convertTypeParamsToTokens(declaration.typeParameters),
            this.convertType(declaration.type)
        )
    }

    private convertPropertyAccessExpression(propertyAccessExpression: ts.PropertyAccessExpression): PropertyAccessDeclaration  {
        let convertedExpression: HeritageSymbol | null;
        if (ts.isIdentifier(propertyAccessExpression.expression)) {
            convertedExpression = this.astFactory.createIdentifierDeclaration(propertyAccessExpression.expression.text)
        } else if (ts.isPropertyAccessExpression(propertyAccessExpression.expression)) {
            convertedExpression = this.convertPropertyAccessExpression(propertyAccessExpression.expression)
        } else {
            // TODO: we can not have errors to be honest
            throw new Error("never supposed to be there")
        }

        return this.astFactory.createPropertyAccessDeclaration(
            this.astFactory.createIdentifierDeclaration(propertyAccessExpression.name.text),
            convertedExpression
        )
    }

    convertHeritageClauses(heritageClauses: ts.NodeArray<ts.HeritageClause> | undefined): Array<HeritageClauseDeclaration> {
        let parentEntities: Array<HeritageClauseDeclaration> = [];


            if (heritageClauses) {
                for (let heritageClause of heritageClauses) {

                    let extending = heritageClause.token == ts.SyntaxKind.ExtendsKeyword;

                    for (let type of heritageClause.types) {
                        let typeArguments: Array<TokenDeclaration> = [];

                        if (type.typeArguments) {
                            for (let typeArgument of type.typeArguments) {
                                let value = (this.convertType(typeArgument) as any).value;
                                this.registerDeclaration(this.astFactory.createTokenDeclaration(value), typeArguments)
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

    convertDeclarations(statements: ts.NodeArray<ts.Node>) : Array<Declaration> {
        var declarations: Declaration[] = [];
        for (let statement of statements) {
            if (ts.isEnumDeclaration(statement)) {
                let enumTokens = statement.members.map(member =>
                    this.astFactory.createEnumTokenDeclaration(
                        member.name.getText(),
                        member.initializer ? member.initializer.getText() : ""
                    ));

                this.registerDeclaration(this.astFactory.createEnumDeclaration(
                    statement.name.getText(),
                    enumTokens
                ), declarations)
            } else if (ts.isVariableStatement(statement)) {
                for (let declaration of statement.declarationList.declarations) {
                    this.registerDeclaration(this.astFactory.declareVariable(
                        declaration.name.getText(),
                        this.convertType(declaration.type),
                        this.convertModifiers(statement.modifiers),
                        this.exportContext.getUID(declaration)
                    ), declarations);
                }
            } else if (ts.isTypeAliasDeclaration(statement)) {
                if (ts.isTypeLiteralNode(statement.type)) {
                    this.registerDeclaration(
                        this.convertTypeLiteralToInterfaceDeclaration(
                            statement.name.getText(),
                            statement.type as ts.TypeLiteralNode,
                            statement.typeParameters
                        ), declarations
                    );
                } else {
                    this.registerDeclaration(this.convertTypeAliasDeclaration(statement), declarations);
                }
            } else if (ts.isClassDeclaration(statement)) {
                if (statement.name != undefined) {

                    let uid = this.exportContext.getUID(statement);

                    this.registerDeclaration(
                        this.astFactory.createClassDeclaration(
                            statement.name.getText(),
                            this.convertClassElementsToMembers(statement.members),
                            this.convertTypeParams(statement.typeParameters),
                            this.convertHeritageClauses(statement.heritageClauses),
                            this.convertModifiers(statement.modifiers),
                            uid
                        ), declarations
                    );
                }

            } else if (ts.isFunctionDeclaration(statement)) {
                let convertedFunctionDeclaration = this.convertFunctionDeclaration(statement);
                if (convertedFunctionDeclaration != null) {
                    this.registerDeclaration(convertedFunctionDeclaration, declarations)
                }
            } else if (ts.isInterfaceDeclaration(statement)) {
                let parentEntities: Array<InterfaceDeclaration> = [];

                this.registerDeclaration(
                    this.astFactory.createInterfaceDeclaration(
                        statement.name.getText(),
                        this.convertMembersToInterfaceMemberDeclarations(statement.members),
                        this.convertTypeParams(statement.typeParameters),
                        this.convertHeritageClauses(statement.heritageClauses),
                        this.exportContext.getUID(statement)
                    ), declarations
                )
            } else if (ts.isModuleDeclaration(statement)) {
                this.convertModule(statement).forEach(d => this.registerDeclaration(d, declarations));
            } else if (ts.isExportAssignment(statement)) {
                let expression = statement.expression;
                if (ts.isIdentifier(expression) || ts.isPropertyAccessExpression(expression)) {
                    let symbol = this.typeChecker.getSymbolAtLocation(expression);

                    if (symbol) {

                        if (symbol.flags & ts.SymbolFlags.Alias) {
                             symbol = this.typeChecker.getAliasedSymbol(symbol);
                        }

                        let declaration = symbol.declarations[0];

                        let uid = this.exportContext.getUID(declaration);
                        this.registerDeclaration(
                            this.astFactory.createExportAssignmentDeclaration(
                                uid, !!statement.isExportEquals
                            ), declarations)
                    }
                }  else {
                    console.log("SKIPPING UNKNOWN EXPRESSION ASSIGNMENT", expression.kind)
                }
            } else if (ts.isImportEqualsDeclaration(statement)) {
                if (ts.isEntityName(statement.moduleReference)) {
                    let moduleReferenceDeclaration = this.convertEntityName(statement.moduleReference);
                    this.registerDeclaration(this.astFactory.createImportEqualsDeclaration(
                        statement.name.getText(),
                        moduleReferenceDeclaration as ModuleReferenceDeclaration
                    ), declarations)
                } else {
                    println(`[TS] skipping external module reference ${statement.moduleReference.getText()}`)
                }
            } else {
                console.log("SKIPPING ", statement.kind);
            }

        }

        return declarations;
    }


    convertModule(module: ts.ModuleDeclaration): Array<Declaration> {
        var declarations: Declaration[] = [];
        if (module.body) {
            let body = module.body;
            let modifiers = this.convertModifiers(module.modifiers);
            let uid = this.exportContext.getUID(module);
            if (ts.isModuleBlock(body)) {
                let moduleDeclarations = this.convertDeclarations(body.statements);
                this.registerDeclaration(this.createDocumentRoot(module.name.getText(), moduleDeclarations, modifiers, uid), declarations);
            } else if (ts.isModuleDeclaration(body)) {
                this.registerDeclaration(this.createDocumentRoot(module.name.getText(), this.convertModule(body), modifiers, uid), declarations);
            }
        }
        return declarations
    }

}