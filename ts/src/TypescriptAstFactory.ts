class TypescriptAstFactory implements AstFactory {

    constructor(private astFactory: AstFactory) {
    }

    createDocumentRoot(packageName: string, declarations: Declaration[]): DocumentRoot {
        return this.astFactory.createDocumentRoot(packageName, declarations);
    }

    createClassDeclaration(name: String, members: Array<MemberDeclaration> = [], typeParams: Array<TypeParameter> = []): ClassDeclaration {
        return this.astFactory.createClassDeclaration(name, members, typeParams);
    }

    createInterfaceDeclaration(name: String, members: Array<MemberDeclaration>, typeParams: Array<TypeParameter>, parentEntities: Array<InterfaceDeclaration> = []): InterfaceDeclaration {
        return this.astFactory.createInterfaceDeclaration(name, members, typeParams, parentEntities);
    }


    createExpression(kind: TypeDeclaration, meta: String): Expression {
        return this.astFactory.createExpression(kind, meta);
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
                this.resolveType(nativeVariableDeclaration.type)
            );
        }

        return null;
    }

    convertTypeParams(nativeTypeDeclarations: ts.NodeArray<ts.TypeParameterDeclaration> | undefined) : Array<TypeParameter> {
        let typeParameterDeclarations: Array<TypeParameter> = [];

        if (nativeTypeDeclarations) {
            typeParameterDeclarations = nativeTypeDeclarations.map(typeParam => {
                const constraint = typeParam.constraint;
                return this.createTypeParam(typeParam.name.getText(), constraint ? [
                    this.resolveType(constraint)
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
            return this.createFunctionDeclaration(
                functionDeclaration.name ? functionDeclaration.name.getText() : "",
                parameterDeclarations,
                functionDeclaration.type ?
                    this.resolveType(functionDeclaration.type) : this.createTypeDeclaration("Unit"),
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
                    this.resolveType(functionDeclaration.type) : this.createTypeDeclaration("Unit"),
                typeParameterDeclarations, false, false
            );
        }

        return null;
    }


    createMethodDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParams: Array<TypeParameter>, override: boolean = false, operator: boolean = false): MethodDeclaration {
        return this.astFactory.createMethodDeclaration(name, parameters, type, typeParams, override, operator);
    }


    createFunctionDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParams: Array<TypeParameter>): FunctionDeclaration {
        return this.astFactory.createFunctionDeclaration(name, parameters, type, typeParams);
    }

    createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValue): FunctionTypeDeclaration {
        return this.astFactory.createFunctionTypeDeclaration(parameters, type);
    }

    createTypeDeclaration(value: string, params: Array<ParameterValue> = []): TypeDeclaration {
        return this.astFactory.createTypeDeclaration(value, params);
    }

    createParameterDeclaration(name: string, type: ParameterValue, initializer: Expression | null): ParameterDeclaration {
        return this.astFactory.createParameterDeclaration(name, type, initializer);
    }

    declareVariable(value: string, type: ParameterValue): VariableDeclaration {
        return this.astFactory.declareVariable(value, type);
    }

    declareProperty(value: string, type: ParameterValue, typeParams: Array<TypeParameter> = [], getter: boolean = false, setter: boolean = false): PropertyDeclaration {
        return this.astFactory.declareProperty(value, type, typeParams, getter, setter);
    }

    createUnionType(params: Array<TypeDeclaration>) {
        return this.createTypeDeclaration("@@Union", params);
    }

    createNullableType(type: TypeDeclaration) : ParameterValue {
        return this.createUnionType([type, this.createTypeDeclaration("null")])
    }

    createVarargType(type: ParameterValue) : ParameterValue {
        return this.createTypeDeclaration("@@Vararg", [type]);
    }

    createTypeParam(name: string, constraints: Array<ParameterValue>): TypeParameter {
        return this.astFactory.createTypeParam(name, constraints);
    }


    resolveType(type: ts.TypeNode | undefined) : ParameterValue {
        if (type == undefined) {
            return this.createTypeDeclaration("Any")
        } else {
            if (type.kind == ts.SyntaxKind.VoidKeyword) {
                return this.createTypeDeclaration("Unit")
            } else if (ts.isArrayTypeNode(type)) {
                let arrayType = type as ts.ArrayTypeNode;
                return this.createTypeDeclaration("@@ArraySugar", [
                    this.resolveType(arrayType.elementType)
                ] as Array<TypeDeclaration>)
            } else {
                if (ts.isUnionTypeNode(type)) {
                    let unionTypeNode = type as ts.UnionTypeNode;
                    let params = unionTypeNode.types
                        .map(argumentType => this.resolveType(argumentType)) as Array<TypeDeclaration>;

                    return this.createUnionType(params)
                } else if (type.kind == ts.SyntaxKind.TypeReference) {
                    let typeReferenceNode = type as ts.TypeReferenceNode;
                    if (typeof typeReferenceNode.typeArguments != "undefined") {
                        let params = typeReferenceNode.typeArguments
                            .map(argumentType => this.resolveType(argumentType)) as Array<TypeDeclaration>;

                        return this.createTypeDeclaration(typeReferenceNode.typeName.getText(), params)
                    } else {
                        return this.createTypeDeclaration(typeReferenceNode.typeName.getText())
                    }
                } else if (type.kind == ts.SyntaxKind.ParenthesizedType) {
                    let parenthesizedTypeNode = type as ts.ParenthesizedTypeNode;
                    return this.resolveType(parenthesizedTypeNode.type);
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
                    return this.createFunctionTypeDeclaration(parameterDeclarations, this.resolveType(functionDeclaration.type))
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

        let functionTypeDeclaration = this.createFunctionTypeDeclaration(
            parameterDeclarations,
            methodSignature.type ? this.resolveType(methodSignature.type) : this.createTypeDeclaration("Unit")
        );

        return this.declareProperty(
            this.convertName(methodSignature.name) as string,
            this.createNullableType(functionTypeDeclaration),
            this.convertTypeParams(methodSignature.typeParameters),
            true
        );
    }

    convertParameterDeclaration(param: ts.ParameterDeclaration) : ParameterDeclaration {
        let initializer = null;
        if (param.initializer != null) {
            initializer = this.createExpression(
                this.createTypeDeclaration("@@DEFINED_EXTERNALLY"),
                param.initializer.getText()
            )
        } else if (param.questionToken != null) {
            initializer = this.createExpression(
                this.createTypeDeclaration("@@DEFINED_EXTERNALLY"),
                "null"
            )
        }

        let paramType = this.resolveType(param.type);
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


}