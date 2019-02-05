declare class AstFactoryV8 implements AstFactory {
    createEnumDeclaration(name: String, values: Array<EnumTokenDeclaration>): EnumDeclaration;
    createEnumTokenDeclaration(value: String, meta: String): EnumTokenDeclaration;
    createExportAssignmentDeclaration(name: string, modifiers: Array<ModifierDeclaration>): ExportAssignmentDeclaration;
    createTokenDeclaration(value: string): TokenDeclaration;
    createHeritageClauseDeclaration(name: string, typeArguments: Array<TokenDeclaration>, extending: boolean): HeritageClauseDeclaration;
    createTypeAliasDeclaration(aliasName: string, typeParams: Array<TokenDeclaration>, typeReference: ParameterValue): TypeAliasDeclaration;
    createCallSignatureDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParams: Array<TypeParameter>): CallSignatureDeclaration;
    createClassDeclaration(name: string, methods: Array<MemberDeclaration>, typeParams: Array<TypeParameter>, parentEntities: Array<ClassLikeDeclaration>, modifiers: Array<ModifierDeclaration>, uid: string): ClassDeclaration;
    createDocumentRoot(packageName: string, declarations: Declaration[], modifiers: Array<ModifierDeclaration>): DocumentRoot;
    createExpression(kind: TypeDeclaration, meta: string): Expression;
    createConstructorDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParams: Array<TypeParameter>, modifiers: Array<ModifierDeclaration>): ConstructorDeclaration;
    createFunctionDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParams: Array<TypeParameter>, modifiers: Array<ModifierDeclaration>, uid: String): FunctionDeclaration;
    createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValue): FunctionTypeDeclaration;
    createIndexSignatureDeclaration(indexTypes: Array<ParameterDeclaration>, returnType: ParameterValue): IndexSignatureDeclaration;
    createInterfaceDeclaration(name: string, methods: Array<MemberDeclaration>, typeParams: Array<TypeParameter>, parentEntities: Array<InterfaceDeclaration>): InterfaceDeclaration;
    createMethodDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParams: Array<TypeParameter>): FunctionDeclaration;
    createMethodSignatureDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParams: Array<TypeParameter>, optional: boolean, modifiers: Array<ModifierDeclaration>): MethodSignatureDeclaration;
    createModifierDeclaration(name: string): ModifierDeclaration;
    createObjectLiteral(methods: Array<MemberDeclaration>): ObjectLiteral;
    createParameterDeclaration(name: string, type: ParameterValue, initializer: Expression | null, vararg: boolean, optional: boolean): ParameterDeclaration;
    createStringTypeDeclaration(tokens: Array<string>): StringTypeDeclaration;
    createUnionTypeDeclaration(params: Array<ParameterValue>): UnionTypeDeclatation;
    createTypeDeclaration(value: string, params: Array<ParameterValue>): TypeDeclaration;
    createTypeParam(name: string, constraints: Array<ParameterValue>): TypeParameter;
    declareProperty(value: string, type: ParameterValue, typeParams: Array<TypeParameter>, optional: boolean, modifiers: Array<ModifierDeclaration>): PropertyDeclaration;
    declareVariable(value: string, type: ParameterValue, modifiers: Array<ModifierDeclaration>, uid: String): VariableDeclaration;
}
