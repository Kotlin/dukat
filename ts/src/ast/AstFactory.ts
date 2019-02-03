declare interface AstFactory {
    createTokenDeclaration(value: string): TokenDeclaration;
    createHeritageClauseDeclaration(name: string, typeArguments: Array<TokenDeclaration>, extending: boolean): HeritageClauseDeclaration

    createTypeAliasDeclaration(aliasName: string, typeParams: Array<TokenDeclaration>, typeReference: ParameterValue): TypeAliasDeclaration;

    createStringTypeDeclaration(tokens: Array<string>): StringTypeDeclaration;
    createIndexSignatureDeclaration(indexTypes: Array<ParameterDeclaration>, returnType: ParameterValue): IndexSignatureDeclaration;

    createModifierDeclaration(name: string): ModifierDeclaration;

    createClassDeclaration(name: string, methods: Array<MemberDeclaration>, typeParams: Array<TypeParameter>, parentEntities: Array<ClassLikeDeclaration>, modifiers: Array<ModifierDeclaration>): ClassDeclaration;

    createObjectLiteral(methods: Array<MemberDeclaration>): ObjectLiteral
    createInterfaceDeclaration(name: string, methods: Array<MemberDeclaration>, typeParams: Array<TypeParameter>, parentEntities: Array<InterfaceDeclaration>): InterfaceDeclaration;

    createExpression(kind: TypeDeclaration, meta: string): Expression;

    declareVariable(value: string, type: ParameterValue, modifiers: Array<ModifierDeclaration>): VariableDeclaration;
    declareProperty(
        value: string,
        type: ParameterValue,
        typeParams: Array<TypeParameter>,
        optional: boolean,
        modifiers: Array<ModifierDeclaration>
    ): PropertyDeclaration;

    createParameterDeclaration(
        name: string,
        type: ParameterValue,
        initializer: Expression | null,
        vararg: boolean,
        optional: boolean
    ): ParameterDeclaration;

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

    createUnionTypeDeclaration(params: Array<ParameterValue>): UnionTypeDeclatation;
    createTypeDeclaration(value: string, params: Array<ParameterValue>): TypeDeclaration;
    createTypeParam(name: string, constraints: Array<ParameterValue>): TypeParameter;

    createDocumentRoot(packageName: string, declarations: Declaration[]): DocumentRoot;
}