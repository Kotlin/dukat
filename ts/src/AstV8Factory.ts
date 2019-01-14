declare class AstFactoryV8 implements AstFactory {
    createClassDeclaration(name: String, methods: Array<MemberDeclaration>, typeParams: Array<TypeParameter>): ClassDeclaration;
    createInterfaceDeclaration(name: String, methods: Array<MemberDeclaration>, typeParams: Array<TypeParameter>): InterfaceDeclaration;
    createDocumentRoot(declarations: Declaration[]): DocumentRoot;
    createExpression(kind: TypeDeclaration, meta: String): Expression;
    createFunctionDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParams: Array<TypeParameter>): FunctionDeclaration;
    createMethodDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParams: Array<TypeParameter>, operator: boolean): MethodDeclaration;
    createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValue): FunctionTypeDeclaration;
    createTypeDeclaration(value: string, params: Array<ParameterValue>): TypeDeclaration;
    createParameterDeclaration(name: string, type: ParameterValue, initializer: Expression | null): ParameterDeclaration;
    declareVariable(value: string, type: ParameterValue): VariableDeclaration;
    createTypeParam(name: string, constraints: Array<ParameterValue>): TypeParameter;
}
