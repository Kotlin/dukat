declare interface AstFactory {
    createClassDeclaration(name: String, methods: Array<MemberDeclaration>, typeParams: Array<TypeParameter>, parentEntities: Array<ClassLikeDeclaration>, staticMembers: Array<MemberDeclaration>): ClassDeclaration;

    createObjectLiteral(methods: Array<MemberDeclaration>): ObjectLiteral
    createInterfaceDeclaration(name: String, methods: Array<MemberDeclaration>, typeParams: Array<TypeParameter>, parentEntities: Array<InterfaceDeclaration>): InterfaceDeclaration;

    createExpression(kind: TypeDeclaration, meta: String): Expression;

    declareVariable(value: string, type: ParameterValue): VariableDeclaration;
    declareProperty(value: string, type: ParameterValue, typeParams: Array<TypeParameter>, getter: boolean, setter: boolean): PropertyDeclaration;

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
        override: boolean,
        operator: boolean
    ): MethodDeclaration;

    createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValue): FunctionTypeDeclaration;

    createTypeDeclaration(value: string, params: Array<ParameterValue>): TypeDeclaration;
    createTypeParam(name: string, constraints: Array<ParameterValue>): TypeParameter;

    createDocumentRoot(packageName: string, declarations: Declaration[]): DocumentRoot;
}