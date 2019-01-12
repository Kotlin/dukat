declare class AstFactoryV8 implements AstFactory {
    createDocumentRoot(declarations: Declaration[]): DocumentRoot;
    createExpression(kind: TypeDeclaration, meta: String): Expression;
    createFunctionDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParams: Array<TypeParameter>): FunctionDeclaration;
    createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValue): FunctionTypeDeclaration;
    createTypeDeclaration(value: string, params: Array<ParameterValue>): TypeDeclaration;
    createParameterDeclaration(name: string, type: ParameterValue, initializer: Expression | null): ParameterDeclaration;
    declareVariable(value: string, type: ParameterValue): VariableDeclaration;
    createTypeParam(name: string, constraints: Array<ParameterValue>): TypeParameter;
}
