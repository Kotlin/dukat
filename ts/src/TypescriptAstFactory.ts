class TypescriptAstFactory implements AstFactory {

    constructor(private astFactory: AstFactory) {
    }

    createDocumentRoot(declarations: Declaration[]): DocumentRoot {
        return this.astFactory.createDocumentRoot(declarations);
    }

    createExpression(kind: TypeDeclaration, meta: String): Expression {
        return this.astFactory.createExpression(kind, meta);
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
                        param => this.createParamDeclaration(param)
                    );
                    return this.createFunctionTypeDeclaration(parameterDeclarations, this.resolveType(functionDeclaration.type))
                } else {
                    return this.createTypeDeclaration(`__UNKNOWN__:${type.kind}`)
                }
            }
        }
    }

    createParamDeclaration(param: ts.ParameterDeclaration) : ParameterDeclaration {
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