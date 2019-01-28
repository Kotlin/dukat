declare interface AstFactory {

    createStringTypeDeclaration(tokens: Array<string>): StringTypeDeclaration;
    createIndexSignatureDeclaration(indexTypes: Array<ParameterDeclaration>, returnType: ParameterValue): IndexSignatureDeclaration;

    createModifierDeclaration(name: string): ModifierDeclaration;

    createClassDeclaration(name: string, methods: Array<MemberDeclaration>, typeParams: Array<TypeParameter>, parentEntities: Array<ClassLikeDeclaration>, staticMembers: Array<MemberDeclaration>): ClassDeclaration;

    createObjectLiteral(methods: Array<MemberDeclaration>): ObjectLiteral
    createInterfaceDeclaration(name: string, methods: Array<MemberDeclaration>, typeParams: Array<TypeParameter>, parentEntities: Array<InterfaceDeclaration>): InterfaceDeclaration;

    createExpression(kind: TypeDeclaration, meta: string): Expression;

    declareVariable(value: string, type: ParameterValue): VariableDeclaration;
    declareProperty(
        value: string,
        type: ParameterValue,
        typeParams: Array<TypeParameter>,
        optional: boolean,
        modifiers: Array<ModifierDeclaration>
    ): PropertyDeclaration;

    createParameterDeclaration(name: string, type: ParameterValue, initializer: Expression | null): ParameterDeclaration;

    createConstructorDeclaration(
        parameters: Array<ParameterDeclaration>,
        type: ParameterValue,
        typeParams: Array<TypeParameter>,
        modifiers: Array<ModifierDeclaration>
    ): ConstructorDeclaration;

    createFunctionDeclaration(
        name: string,
        parameters: Array<ParameterDeclaration>,
        type: ParameterValue,
        typeParams: Array<TypeParameter>,
        modifiers: Array<ModifierDeclaration>
    ): FunctionDeclaration;

    createMethodSignatureDeclaration(
        name: string,
        parameters: Array<ParameterDeclaration>,
        type: ParameterValue,
        typeParams: Array<TypeParameter>,
        optional: boolean,
        modifiers: Array<ModifierDeclaration>
    ): MethodSignatureDeclaration;

    createCallSignatureDeclaration(
        parameters: Array<ParameterDeclaration>,
        type: ParameterValue,
        typeParams: Array<TypeParameter>
    ): CallSignatureDeclaration;


    createMethodDeclaration(
        name: string,
        parameters: Array<ParameterDeclaration>,
        type: ParameterValue,
        typeParams: Array<TypeParameter>,
    ): FunctionDeclaration;

    createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValue): FunctionTypeDeclaration;

    createTypeDeclaration(value: string, params: Array<ParameterValue>): TypeDeclaration;
    createTypeParam(name: string, constraints: Array<ParameterValue>): TypeParameter;

    createDocumentRoot(packageName: string, declarations: Declaration[]): DocumentRoot;
}