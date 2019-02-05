class AstConverter {

    constructor(private astFactory: AstFactory, private typeChecker: ts.TypeChecker) {
    }

    createDocumentRoot(packageName: string, declarations: Declaration[], modifiers: Array<ModifierDeclaration>): DocumentRoot {
        return this.astFactory.createDocumentRoot(packageName, declarations, modifiers);
    }

    convertName(name: ts.BindingName | ts.PropertyName) : string | null {
        if (ts.isNumericLiteral(name)) {
            return "`" + name.getText() + "`";
        } else if (ts.isIdentifier(name)) {
            return name.getText();
        }
        return null
    }

    convertVariable(nativeVariableDeclaration: ts.VariableDeclaration & ts.PropertyDeclaration & ts.ParameterDeclaration) : VariableDeclaration | null {
        let name = this.convertName(nativeVariableDeclaration.name);

        if (name != null) {
            return this.astFactory.declareVariable(
                name,
                this.convertType(nativeVariableDeclaration.type),
                this.convertModifiers(nativeVariableDeclaration.modifiers)
            );
        }

        return null;
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
            return this.astFactory.createFunctionDeclaration(
                functionDeclaration.name ? functionDeclaration.name.getText() : "",
                parameterDeclarations,
                functionDeclaration.type ?
                    this.convertType(functionDeclaration.type) : this.createTypeDeclaration("Unit"),
                typeParameterDeclarations,
                this.convertModifiers(functionDeclaration.modifiers)
            );
        }

        return null;
    }

    convertModifiers(nativeModifiers: ts.NodeArray<ts.Modifier> | undefined): Array<ModifierDeclaration> {
        var res: Array<ModifierDeclaration> = [];

        if (nativeModifiers) {
            nativeModifiers.forEach(modifier => {
                if (modifier.kind == ts.SyntaxKind.StaticKeyword) {
                    res.push(this.astFactory.createModifierDeclaration("STATIC"));
                } else if (modifier.kind == ts.SyntaxKind.DeclareKeyword) {
                    res.push(this.astFactory.createModifierDeclaration("DECLARE"));
                } else if (modifier.kind == ts.SyntaxKind.ExportKeyword) {
                    res.push(this.astFactory.createModifierDeclaration("EXPORT"));
                } else if (modifier.kind == ts.SyntaxKind.DefaultKeyword) {
                    res.push(this.astFactory.createModifierDeclaration("DEFAULT"));
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
        return this.astFactory.createFunctionDeclaration(name, parameters, type, typeParams, modifiers);
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
        return this.createTypeDeclaration("@@Intersection", params);
    }

    convertType(type: ts.TypeNode | ts.TypeElement | undefined) : ParameterValue {
        if (type == undefined) {
            return this.createTypeDeclaration("Any")
        } else {
            if (type.kind == ts.SyntaxKind.VoidKeyword) {
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
                } else if (type.kind == ts.SyntaxKind.TypeReference) {
                    let typeReferenceNode = type as ts.TypeReferenceNode;
                    if (typeof typeReferenceNode.typeArguments != "undefined") {
                        let params = typeReferenceNode.typeArguments
                            .map(argumentType => this.convertType(argumentType)) as Array<TypeDeclaration>;

                        return this.createTypeDeclaration(typeReferenceNode.typeName.getText(), params)
                    } else {
                        return this.createTypeDeclaration(typeReferenceNode.typeName.getText())
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
                    return this.createTypeDeclaration("@@SELF_REFERENCE")
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

        res.push(
            this.astFactory.createIndexSignatureDeclaration(
                parameterDeclarations,
                this.convertType(indexSignatureDeclaration.type)
            )
        );

        return res;
    }

    convertTypeElementToInterfaceMemberDeclarations(member: ts.TypeElement) : Array<MemberDeclaration> {
        let  res: Array<MemberDeclaration> = [];
        if (ts.isMethodSignature(member)) {
            let methodDeclaration = this.convertTypeElementToMethodSignatureDeclaration(member);
            if (methodDeclaration) {
                res.push(methodDeclaration);
            }
        } else if (ts.isPropertySignature(member)) {
            res.push(
                this.convertPropertySignature(member)
            );
        } else if (ts.isIndexSignatureDeclaration(member)) {
            this.convertIndexSignature(member as ts.IndexSignatureDeclaration).forEach(member =>
                res.push(member)
            );
        } else if (ts.isCallSignatureDeclaration(member)) {
            res.push(
                this.astFactory.createCallSignatureDeclaration(
                    this.convertParameterDeclarations(member.parameters),
                    member.type ? this.convertType(member.type) : this.createTypeDeclaration("Unit"),
                    this.convertTypeParams(member.typeParameters)
                )
            );
        }

        return res;
    }

    convertMembersToInterfaceMemberDeclarations(members: ts.NodeArray<ts.TypeElement>) : Array<MemberDeclaration> {
        let res: Array<MemberDeclaration> = [];
        members.map(member => {
            this.convertTypeElementToInterfaceMemberDeclarations(member).map(memberDeclaration => {
                res.push(memberDeclaration);
            });
        });

        return res;
    }

    convertClassElementsToClassMembers(classDeclarationMembers: ts.NodeArray<ts.ClassElement> | null) : Array<MemberDeclaration> {
        if (classDeclarationMembers == null) {
            return [];
        }

        let members: Array<MemberDeclaration> = [];

        for (let memberDeclaration of classDeclarationMembers) {
            if (ts.isIndexSignatureDeclaration(memberDeclaration)) {
                this.convertIndexSignature(memberDeclaration as ts.IndexSignatureDeclaration).map(member => {
                    members.push(member);
                });
            } else if (ts.isPropertyDeclaration(memberDeclaration)) {
                let propertyDeclaration = this.convertPropertyDeclaration(
                    memberDeclaration as ts.PropertyDeclaration
                );
                if (propertyDeclaration != null) {
                    members.push(propertyDeclaration);
                }
            } else if (ts.isMethodDeclaration(memberDeclaration)) {
                let methodDeclaration = memberDeclaration as (ts.FunctionDeclaration & ts.MethodDeclaration & ts.MethodSignature);
                let convertedMethodDeclaration = this.convertMethodDeclaration(methodDeclaration);
                if (convertedMethodDeclaration != null) {
                    members.push(convertedMethodDeclaration);
                }
            } else if (memberDeclaration.kind == ts.SyntaxKind.Constructor) {
                this.convertConstructorDeclaration(memberDeclaration as ts.ConstructorDeclaration).map(member => {
                    members.push(member);
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
            []
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
                        res.push(convertedVariable);
                    }
                }
            }

            params.push(this.convertParameterDeclaration(parameter, count));
        });

        res.push(this.astFactory.createConstructorDeclaration(
            params,
            this.createTypeDeclaration("______"),
            this.convertTypeParams(constructorDeclaration.typeParameters), this.convertModifiers(constructorDeclaration.modifiers))
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
                                typeArguments.push(this.astFactory.createTokenDeclaration(value))
                            }
                        }

                        parentEntities.push(
                            this.astFactory.createHeritageClauseDeclaration(
                                type.expression.getText(),
                                typeArguments,
                                extending
                            )
                        );
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
                    this.astFactory.createEnumTokenDeclaration(member.name.getText(), ""));

                declarations.push(this.astFactory.createEnumDeclaration(
                    statement.name.getText(),
                    enumTokens
                ))
            } else if (ts.isVariableStatement(statement)) {
                for (let declaration of statement.declarationList.declarations) {
                    declarations.push(this.astFactory.declareVariable(
                        declaration.name.getText(),
                        this.convertType(declaration.type),
                        this.convertModifiers(statement.modifiers)
                    ));
                }
            } else if (ts.isTypeAliasDeclaration(statement)) {
                if (ts.isTypeLiteralNode(statement.type)) {
                    declarations.push(
                        this.convertTypeLiteralToInterfaceDeclaration(
                            statement.name.getText(),
                            statement.type as ts.TypeLiteralNode,
                            statement.typeParameters
                        )
                    );
                } else {
                    declarations.push(this.convertTypeAliasDeclaration(statement));
                }
            } else if (ts.isClassDeclaration(statement)) {
                if (statement.name != undefined) {
                    declarations.push(
                        this.astFactory.createClassDeclaration(
                            statement.name.getText(),
                            this.convertClassElementsToClassMembers(statement.members),
                            this.convertTypeParams(statement.typeParameters),
                            this.convertHeritageClauses(statement.heritageClauses),
                            this.convertModifiers(statement.modifiers)
                        )
                    );
                }

            } else if (ts.isFunctionDeclaration(statement)) {
                let convertedFunctionDeclaration = this.convertFunctionDeclaration(statement);
                if (convertedFunctionDeclaration != null) {
                    declarations.push(convertedFunctionDeclaration)
                }
            } else if (ts.isInterfaceDeclaration(statement)) {
                let interfaceDeclaration = statement as ts.InterfaceDeclaration;
                let parentEntities: Array<InterfaceDeclaration> = [];

                declarations.push(
                    this.astFactory.createInterfaceDeclaration(
                        interfaceDeclaration.name.getText(),
                        this.convertMembersToInterfaceMemberDeclarations(interfaceDeclaration.members),
                        this.convertTypeParams(interfaceDeclaration.typeParameters),
                        this.convertHeritageClauses(interfaceDeclaration.heritageClauses)
                    )
                )
            } else if (ts.isModuleDeclaration(statement)) {
                this.convertModule(statement).forEach(d => declarations.push(d));
            } else if (ts.isExportAssignment(statement)) {
                let expression = statement.expression;
                if (ts.isIdentifier(expression)) {
                    declarations.push(this.astFactory.createExportAssignmentDeclaration(expression.text))
                } else {
                    console.log("SKIPPING UNKNOWN EXPRESSION ASSIGNMENT", expression.kind)
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
            if (ts.isModuleBlock(body)) {
                let moduleDeclarations = this.convertDeclarations(body.statements);
                declarations.push(this.createDocumentRoot(module.name.getText(), moduleDeclarations, modifiers));
            } else if (ts.isModuleDeclaration(body)) {
                declarations.push(this.createDocumentRoot(module.name.getText(), this.convertModule(body), modifiers));
            }
        }
        return declarations
    }


}