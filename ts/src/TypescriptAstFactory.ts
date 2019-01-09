class TypescriptAstFactory implements AstFactory {

    constructor(private astFactory: AstFactory) {
    }

    createDocumentRoot(declarations: Declaration[]): DocumentRoot {
        return this.astFactory.createDocumentRoot(declarations);
    }

    createExpression(kind: TypeDeclaration, meta: String): Expression {
        return this.astFactory.createExpression(kind, meta);
    }

    createFunctionDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: TypeDeclaration): FunctionDeclaration {
        return this.astFactory.createFunctionDeclaration(name, parameters, type);
    }

    createTypeDeclaration(value: string, params: Array<TypeDeclaration> = []): TypeDeclaration {
        return this.astFactory.createTypeDeclaration(value, params);
    }

    createParameterDeclaration(name: string, type: TypeDeclaration, initializer: Expression | null): ParameterDeclaration {
        return this.astFactory.createParameterDeclaration(name, type, initializer);
    }

    declareVariable(value: string, type: TypeDeclaration): VariableDeclaration {
        return this.astFactory.declareVariable(value, type);
    }

    createUnionType(params: Array<TypeDeclaration>) {
        return this.createTypeDeclaration("@@Union", params);
    }

    createNullableType(type: TypeDeclaration) {
        return this.createUnionType([type, this.createTypeDeclaration("null")])
    }

}