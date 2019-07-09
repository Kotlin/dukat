declare interface AstFactory {

    createDefinitionInfoDeclaration(fileName: string): DefinitionInfoDeclaration;

    createTupleDeclaration(params: Array<ParameterValue>): TupleDeclaration;

    createImportEqualsDeclaration(name: string, moduleReference: ModuleReferenceDeclaration, uid: string): ImportEqualsDeclaration

    createIdentifierDeclaration(value: string): IdentifierDeclaration;

    createQualifiedNameDeclaration(left: ParameterValue, right: IdentifierDeclaration): QualifierDeclaration;

    createThisTypeDeclaration(): ThisTypeDeclaration;

    createEnumDeclaration(name: String, values: Array<EnumTokenDeclaration>): EnumDeclaration;
    createEnumTokenDeclaration(value: String, meta: String): EnumTokenDeclaration;

    createExportAssignmentDeclaration(name: string, isExportEquals: boolean): ExportAssignmentDeclaration;
    createHeritageClauseDeclaration(name: IdentifierDeclaration, typeArguments: Array<ParameterValue>, extending: boolean): HeritageClauseDeclaration

    createTypeAliasDeclaration(
        aliasName: NameDeclaration,
        typeParams: Array<IdentifierDeclaration>,
        typeReference: ParameterValue,
        uid: String
    ): TypeAliasDeclaration;

    createStringLiteralDeclaration(token: string): StringLiteralDeclaration;
    createIndexSignatureDeclaration(indexTypes: Array<ParameterDeclaration>, returnType: ParameterValue): IndexSignatureDeclaration;

    createModifierDeclaration(name: string): ModifierDeclaration;

    createClassDeclaration(name: NameDeclaration, methods: Array<MemberDeclaration>, typeParams: Array<TypeParameter>, parentEntities: Array<ClassLikeDeclaration>, modifiers: Array<ModifierDeclaration>, uid: string): ClassDeclaration;

    createObjectLiteral(methods: Array<MemberDeclaration>): ObjectLiteral
    createInterfaceDeclaration(name: NameDeclaration, methods: Array<MemberDeclaration>, typeParams: Array<TypeParameter>, parentEntities: Array<InterfaceDeclaration>, definitionsInfo: Array<DefinitionInfoDeclaration>, uid: String): InterfaceDeclaration;

    createExpression(kind: TypeDeclaration, meta: string): Expression;

    declareVariable(value: string, type: ParameterValue, modifiers: Array<ModifierDeclaration>, uid: String): VariableDeclaration;
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
        modifiers: Array<ModifierDeclaration>,
        uid: String
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

    createIntersectionTypeDeclaration(params: Array<ParameterValue>): IntersectionTypeDeclaration;
    createUnionTypeDeclaration(params: Array<ParameterValue>): UnionTypeDeclatation;
    createTypeDeclaration(value: IdentifierDeclaration, params: Array<ParameterValue>): TypeDeclaration;
    createTypeParam(name: NameDeclaration, constraints: Array<ParameterValue>): TypeParameter;

    createModuleDeclaration(
        packageName: NameDeclaration,
        declarations: Declaration[],
        modifiers: Array<ModifierDeclaration>,
        definitionsInfo: Array<DefinitionInfoDeclaration>,
        uid: string,
        resourceName: string,
        root: boolean
    ): PackageDeclaration;
    createSourceFileDeclaration(fileName: string, root: PackageDeclaration, referencedFiles: Array<IdentifierDeclaration>): SourceFileDeclaration;

    createSourceSet(sources: Array<SourceFileDeclaration>): SourceSet
}