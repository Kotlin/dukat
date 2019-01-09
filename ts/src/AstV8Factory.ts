declare class AstFactoryV8 implements AstFactory {
    createDocumentRoot(declarations: Declaration[]): DocumentRoot;
    createExpression(kind: TypeDeclaration, meta: String): Expression;
    createFunctionDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: TypeDeclaration): FunctionDeclaration;
    createTypeDeclaration(value: string, params: Array<TypeDeclaration>): TypeDeclaration;
    createParameterDeclaration(name: string, type: TypeDeclaration, initializer: Expression | null): ParameterDeclaration;
    declareVariable(value: string, type: TypeDeclaration): VariableDeclaration;
}
