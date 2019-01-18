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

    convertFunctionDeclaration(functionDeclaration: ts.FunctionDeclaration & ts.MethodDeclaration) : FunctionDeclaration | null  {
        let typeParameterDeclarations: Array<TypeParameter> = this.convertTypeParams(functionDeclaration.typeParameters);

        let parameterDeclarations = functionDeclaration.parameters
            .map(
            param => this.convertParameterDeclaration(param)
            );


        if (ts.isIdentifier(functionDeclaration.name)) {
            return this.astFactory.createFunctionDeclaration(
                functionDeclaration.name ? functionDeclaration.name.getText() : "",
                parameterDeclarations,
                functionDeclaration.type ?
                    this.convertType(functionDeclaration.type) : this.createTypeDeclaration("Unit"),
                typeParameterDeclarations
            );
        }

        return null;
    }

    convertMethodDeclaration(functionDeclaration: ts.FunctionDeclaration & ts.MethodDeclaration & ts.MethodSignature) : MethodDeclaration | null  {
        let typeParameterDeclarations: Array<TypeParameter> = this.convertTypeParams(functionDeclaration.typeParameters);

        let parameterDeclarations = functionDeclaration.parameters
            .map(
                param => this.convertParameterDeclaration(param)
            );


        if (ts.isIdentifier(functionDeclaration.name)) {
            return this.createMethodDeclaration(
                functionDeclaration.name ? functionDeclaration.name.getText() : "",
                parameterDeclarations,
                functionDeclaration.type ?
                    this.convertType(functionDeclaration.type) : this.createTypeDeclaration("Unit"),
                typeParameterDeclarations, false, false
            );
        }

        return null;
    }


    createMethodDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParams: Array<TypeParameter>, override: boolean = false, operator: boolean = false): MethodDeclaration {
        return this.astFactory.createMethodDeclaration(name, parameters, type, typeParams, override, operator);
    }

    private createTypeDeclaration(value: string, params: Array<ParameterValue> = []): TypeDeclaration {
        return this.astFactory.createTypeDeclaration(value, params);
    }

    createParameterDeclaration(name: string, type: ParameterValue, initializer: Expression | null): ParameterDeclaration {
        return this.astFactory.createParameterDeclaration(name, type, initializer);
    }

    private createProperty(value: string, type: ParameterValue, typeParams: Array<TypeParameter> = [], getter: boolean = false, setter: boolean = false): PropertyDeclaration {
        return this.astFactory.declareProperty(value, type, typeParams, getter, setter);
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

    createVarargType(type: ParameterValue) : ParameterValue {
        return this.createTypeDeclaration("@@Vararg", [type]);
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
                        param => this.convertParameterDeclaration(param)
                    );
                    return this.astFactory.createFunctionTypeDeclaration(parameterDeclarations, this.convertType(functionDeclaration.type))
                } else {
                    return this.createTypeDeclaration(`__UNKNOWN__:${type.kind}`)
                }
            }
        }
    }

    convertParameterDeclarations(parameters: ts.NodeArray<ts.ParameterDeclaration>) : Array<ParameterDeclaration> {
        return parameters.map(parameter => this.convertParameterDeclaration(parameter));
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

    convertParameterDeclaration(param: ts.ParameterDeclaration) : ParameterDeclaration {
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
        if (param.dotDotDotToken) {
            paramType = this.createVarargType(paramType);
        }

        return this.createParameterDeclaration(
            param.name.getText(),
            paramType,
            initializer
        )
    }

    convertTypeElementToMemberDeclaration(node: ts.TypeElement) : MemberDeclaration | null {
        let methodDeclaration = node as (ts.FunctionDeclaration & ts.MethodDeclaration & ts.MethodSignature);

        if (methodDeclaration.questionToken) {
            return this.convertMethodSignatureToPropertyDeclaration(methodDeclaration)
        } else {
            let convertedMethodDeclaration = this.convertMethodDeclaration(methodDeclaration);
            if (convertedMethodDeclaration != null) {
                return convertedMethodDeclaration
            }
        }

        return null;
    }

    convertPropertySignature(node: ts.PropertySignature) : MemberDeclaration  {
        let propertyDeclaration: PropertyDeclaration;
        if (node.questionToken) {
            propertyDeclaration = this.createProperty(
                this.convertName(node.name) as string,
                this.createNullableType(this.convertType(node.type)),
                [], true, true)
        } else {
            propertyDeclaration = this.createProperty(this.convertName(node.name) as string, this.convertType(node.type));
        }

        return propertyDeclaration;
    }

    convertIndexSignature(indexSignatureDeclaration: ts.IndexSignatureDeclaration) : Array<MemberDeclaration> {
        let res: Array<MemberDeclaration> = [];
        let typeParameterDeclarations: Array<TypeParameter> = this.convertTypeParams(indexSignatureDeclaration.typeParameters);
        let parameterDeclarations = indexSignatureDeclaration.parameters
            .map(
                param => this.convertParameterDeclaration(param)
            );

        res.push(this.createMethodDeclaration(
            "get", parameterDeclarations, this.createNullableType(this.convertType(indexSignatureDeclaration.type)), typeParameterDeclarations, false,true
        ));

        parameterDeclarations.push(
            this.createParameterDeclaration("value", this.convertType(indexSignatureDeclaration.type), null)
        );

        res.push(this.createMethodDeclaration(
            "set", parameterDeclarations, this.createTypeDeclaration("Unit"), typeParameterDeclarations, false, true
        ));

        return res;
    }

    convertTypeElementToMemberDeclarations(member: ts.TypeElement) : Array<MemberDeclaration> {
        let  res: Array<MemberDeclaration> = [];
        if (ts.isMethodSignature(member)) {
            let methodDeclaration = this.convertTypeElementToMemberDeclaration(member);
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
                this.createMethodDeclaration(
                    "invoke",
                    this.convertParameterDeclarations(member.parameters),
                    member.type ? this.convertType(member.type) : this.createTypeDeclaration("Unit"),
                    this.convertTypeParams(member.typeParameters),
                    false,
                    true
                )
            );
        }

        return res;
    }

    convertMembersToInterfaceMemberDeclarations(members: ts.NodeArray<ts.TypeElement>) : Array<MemberDeclaration> {
        let res: Array<MemberDeclaration> = [];
        members.map(member => {
            this.convertTypeElementToMemberDeclarations(member).map(memberDeclaration => {
                res.push(memberDeclaration);
            });
        });

        return res;
    }

    convertClassElementsToClassDeclarations(classDeclarationMembers: ts.NodeArray<ts.ClassElement> | null) {
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
                let convertedVariable = this.convertVariable(
                    memberDeclaration as (ts.VariableDeclaration & ts.PropertyDeclaration & ts.ParameterDeclaration)
                );
                if (convertedVariable != null) {
                    members.push(convertedVariable);
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


    convertTypeLiteralToInterfaceDeclaration(name: String, typeLiteral: ts.TypeLiteralNode, typeParams: Array<TypeParameter> = []): InterfaceDeclaration {
        return this.astFactory.createInterfaceDeclaration(
            name,
            this.convertMembersToInterfaceMemberDeclarations(typeLiteral.members),
            typeParams,
            []
        );
    }

    convertConstructorDeclaration(memberDeclaration: ts.ConstructorDeclaration): Array<MemberDeclaration> {
        let constructor = memberDeclaration as ts.ConstructorDeclaration;

        let params: Array<ParameterDeclaration> = [];

        let res: Array<MemberDeclaration> = [];

        for (let parameter of constructor.parameters) {
            if (parameter.modifiers) {
                let isField = parameter.modifiers.some(modifier => modifier.kind == ts.SyntaxKind.PublicKeyword);
                if (isField) {
                    let convertedVariable = this.convertVariable(
                        parameter as (ts.VariableDeclaration & ts.PropertyDeclaration & ts.ParameterDeclaration)
                    );
                    if (convertedVariable != null) {
                        res.push(convertedVariable);
                    }
                }
            }

            params.push(this.convertParameterDeclaration(parameter));
        }


        res.push(this.createMethodDeclaration("@@CONSTRUCTOR",
            params,
            this.createTypeDeclaration("______"), this.convertTypeParams(constructor.typeParameters))
        );

        return res;
    }

    convertDeclarations(statements: Array<ts.Node>) : Array<Declaration> {
        var declarations: Declaration[] = [];
        for (let statement of statements) {
            if (ts.isVariableStatement(statement)) {
                const variableStatment = statement as ts.VariableStatement;

                const hasModifiers = variableStatment.modifiers != undefined;

                if (hasModifiers) {
                    for (let declaration of variableStatment.declarationList.declarations) {
                        declarations.push(this.astFactory.declareVariable(
                            declaration.name.getText(),
                            this.convertType(declaration.type)
                        ));
                    }
                }
            } else if (ts.isTypeAliasDeclaration(statement)) {
                let typeAliasDeclaration = statement as ts.TypeAliasDeclaration;
                if (ts.isTypeLiteralNode(typeAliasDeclaration.type)) {
                    declarations.push(
                        this.convertTypeLiteralToInterfaceDeclaration(
                            typeAliasDeclaration.name.getText(),
                            typeAliasDeclaration.type as ts.TypeLiteralNode,
                            this.convertTypeParams(typeAliasDeclaration.typeParameters)
                        )
                    );
                }
            } else if (ts.isClassDeclaration(statement)) {
                const classDeclaration = statement as ts.ClassDeclaration;
                let parentEntities: Array<ClassLikeDeclaration> = [];

                if (classDeclaration.name != undefined) {

                    if (classDeclaration.heritageClauses) {
                        for (let heritageClause of classDeclaration.heritageClauses) {
                            for (let type of heritageClause.types) {
                                let typeParams: Array<TypeParameter> = [];

                                if (type.typeArguments) {
                                    for (let typeArgument of type.typeArguments) {
                                        let value = (this.convertType(typeArgument) as any).value;
                                        typeParams.push(this.astFactory.createTypeParam(value, []))
                                    }
                                }


                                parentEntities.push(
                                    this.astFactory.createInterfaceDeclaration(type.expression.getText(), [], typeParams, [])
                                );
                            }
                        }
                    }

                    let members: Array<MemberDeclaration> = [];
                    this.convertClassElementsToClassDeclarations(classDeclaration.members).forEach(member => {
                        members.push(member);
                    });

                    declarations.push(
                        this.astFactory.createClassDeclaration(
                            classDeclaration.name.getText(),
                            members,
                            this.convertTypeParams(classDeclaration.typeParameters),
                            parentEntities
                        )
                    );
                }

            } else if (ts.isFunctionDeclaration(statement)) {
                let convertedFunctionDeclaration = this.convertFunctionDeclaration(statement as (ts.FunctionDeclaration & ts.MethodDeclaration));
                if (convertedFunctionDeclaration != null) {
                    declarations.push(convertedFunctionDeclaration)
                }
            } else if (ts.isInterfaceDeclaration(statement)) {
                let interfaceDeclaration = statement as ts.InterfaceDeclaration;
                let parentEntities: Array<InterfaceDeclaration> = [];

                if (interfaceDeclaration.heritageClauses) {
                    for (let heritageClause of interfaceDeclaration.heritageClauses) {
                        for (let type of heritageClause.types) {
                            let typeParams: Array<TypeParameter> = [];

                            if (type.typeArguments) {
                                for (let typeArgument of type.typeArguments) {
                                    let value = (this.convertType(typeArgument) as any).value;
                                    typeParams.push(this.astFactory.createTypeParam(value, []))
                                }
                            }


                            parentEntities.push(
                                this.astFactory.createInterfaceDeclaration(type.expression.getText(), [], typeParams, [])
                            );
                        }
                    }
                }

                declarations.push(
                    this.astFactory.createInterfaceDeclaration(
                        interfaceDeclaration.name.getText(),
                        this.convertMembersToInterfaceMemberDeclarations(interfaceDeclaration.members),
                        this.convertTypeParams(interfaceDeclaration.typeParameters),
                        parentEntities
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