declare interface AstFactory {
    createExpression(kind: TypeDeclaration, meta: String): Expression;

    declareVariable(value: string, type: TypeDeclaration): VariableDeclaration;

    createParameterDeclaration(name: string, type: ParameterValue, initializer: Expression | null): ParameterDeclaration;

    createFunctionDeclaration(
        name: string,
        parameters: Array<ParameterDeclaration>,
        type: ParameterValue,
        typeParams: Array<TypeParameter>
    ): FunctionDeclaration;
    createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValue): FunctionTypeDeclaration;

    createTypeDeclaration(value: string, params: Array<ParameterValue>): TypeDeclaration;
    createTypeParam(name: string, constraints: Array<ParameterValue>): TypeParameter;

    createDocumentRoot(declarations: Declaration[]): DocumentRoot;
}