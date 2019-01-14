declare interface AstFactory {
    createClassDeclaration(name: String, methods: Array<MemberDeclaration>, typeParams: Array<TypeParameter>): ClassDeclaration;
    createInterfaceDeclaration(name: String, methods: Array<MemberDeclaration>, typeParams: Array<TypeParameter>): InterfaceDeclaration;

    createExpression(kind: TypeDeclaration, meta: String): Expression;

    declareVariable(value: string, type: ParameterValue): VariableDeclaration;

    createParameterDeclaration(name: string, type: ParameterValue, initializer: Expression | null): ParameterDeclaration;

    createFunctionDeclaration(
        name: string,
        parameters: Array<ParameterDeclaration>,
        type: ParameterValue,
        typeParams: Array<TypeParameter>
    ): FunctionDeclaration;

    createMethodDeclaration(
        name: string,
        parameters: Array<ParameterDeclaration>,
        type: ParameterValue,
        typeParams: Array<TypeParameter>,
        operator: boolean
    ): MethodDeclaration;

    createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValue): FunctionTypeDeclaration;

    createTypeDeclaration(value: string, params: Array<ParameterValue>): TypeDeclaration;
    createTypeParam(name: string, constraints: Array<ParameterValue>): TypeParameter;

    createDocumentRoot(declarations: Declaration[]): DocumentRoot;
}