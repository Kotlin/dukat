class AstConverter {

    constructor(private astFactory: AstFactory) {
    }

    createDocumentRoot(packageName: string, declarations: Declaration[]): DocumentRoot {
        return this.astFactory.createDocumentRoot(packageName, declarations);
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
                this.convertType(nativeVariableDeclaration.type)
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
        if (!this.nodeIsExportDeclaration(functionDeclaration)) {
            return null;
        }

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
                []
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

    createParameterDeclaration(name: string, type: ParameterValue, initializer: Expression | null, vararg: boolean): ParameterDeclaration {
        return this.astFactory.createParameterDeclaration(name, type, initializer, vararg);
    }

    private createProperty(value: string, type: ParameterValue, typeParams: Array<TypeParameter> = [], optional: boolean): PropertyDeclaration {
        return this.astFactory.declareProperty(value, type, typeParams, optional, []);
    }

    createUnionType(params: Array<TypeDeclaration>) {
        return this.createTypeDeclaration("@@Union", params);
    }

    createIntersectionType(params: Array<TypeDeclaration>) {
        return this.createTypeDeclaration("@@Intersection", params);
    }

    createNullableType(type: TypeDeclaration) : ParameterValue {
        return this.createUnionType([type, this.createTypeDeclaration("null")])
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

                    return this.createUnionType(params)
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

    convertMethodSignatureToPropertyDeclaration(methodSignature: ts.MethodSignature) : PropertyDeclaration {
        let parameterDeclarations = this.convertParameterDeclarations(methodSignature.parameters);

        let functionTypeDeclaration = this.astFactory.createFunctionTypeDeclaration(
            parameterDeclarations,
            methodSignature.type ? this.convertType(methodSignature.type) : this.createTypeDeclaration("Unit")
        );

        return this.createProperty(
            this.convertName(methodSignature.name) as string,
            this.createNullableType(functionTypeDeclaration),
            this.convertTypeParams(methodSignature.typeParameters),
            true
        );
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
        if (param.questionToken) {
            paramType = this.createNullableType(paramType);
        }

        let name = ts.isIdentifier(param.name) ? param.name.getText() : `__${index}`;

        return this.createParameterDeclaration(
            name,
            paramType,
            initializer,
            !!param.dotDotDotToken
        )
    }

    convertTypeElementToMethoSignatureDeclaration(methodDeclaration: ts.MethodSignature) : MethodSignatureDeclaration | null {
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
            let methodDeclaration = this.convertTypeElementToMethoSignatureDeclaration(member);
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

    convertClassElementsToClassDeclarations(classDeclarationMembers: ts.NodeArray<ts.ClassElement> | null) : Array<MemberDeclaration> {
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
                    let isStatic = methodDeclaration.modifiers ?
                        methodDeclaration.modifiers.some(member => member.kind == ts.SyntaxKind.StaticKeyword) : false;

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

    private hasDeclareModifier(node: ts.Node): boolean {
        return node.modifiers ? node.modifiers.some(modifier => modifier.kind == ts.SyntaxKind.DeclareKeyword) : false;
    }

    private hasExportModifier(node: ts.Node): boolean {
        return node.modifiers ? node.modifiers.some(modifier => modifier.kind == ts.SyntaxKind.ExportKeyword) : false;
    }

    private nodeIsExportDeclaration(node: ts.Node): boolean {
        return this.hasDeclareModifier(node) || this.hasExportModifier(node);
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

    convertDeclarations(statements: Array<ts.Node>) : Array<Declaration> {
        var declarations: Declaration[] = [];
        for (let statement of statements) {
            if (ts.isVariableDeclaration(statement)) {
                declarations.push(this.astFactory.declareVariable(
                    statement.name.getText(),
                    this.convertType(statement.type)
                ));
            } else if (ts.isVariableStatement(statement)) {
                if (this.nodeIsExportDeclaration(statement)) {
                    for (let declaration of statement.declarationList.declarations) {
                        declarations.push(this.astFactory.declareVariable(
                            declaration.name.getText(),
                            this.convertType(declaration.type)
                        ));
                    }
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
                const classDeclaration = statement as ts.ClassDeclaration;

                if (classDeclaration.name != undefined) {

                    let members: Array<MemberDeclaration> = [];
                    let staticMembers: Array<MemberDeclaration> = [];

                    this.convertClassElementsToClassDeclarations(classDeclaration.members).forEach(member => {
                        if (false) {
                            staticMembers.push(member);
                        } else {
                            members.push(member);
                        }
                    });

                    declarations.push(
                        this.astFactory.createClassDeclaration(
                            classDeclaration.name.getText(),
                            members,
                            this.convertTypeParams(classDeclaration.typeParameters),
                            this.convertHeritageClauses(classDeclaration.heritageClauses),
                            staticMembers
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
                let moduleDeclaration = statement as ts.ModuleDeclaration;

                if (moduleDeclaration.body) {
                    let moduleStatements: Array<ts.Node> = [];
                    collectChildren(moduleDeclaration.body, moduleStatements);
                    let moduleDeclarations = this.convertDeclarations(moduleStatements);
                    declarations.push(this.createDocumentRoot(moduleDeclaration.name.getText(), moduleDeclarations));
                }

            } else {
                console.log("SKIPPING ", statement.kind);
            }

        }

        return declarations;
    }


}