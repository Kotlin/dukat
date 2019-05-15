package org.jetbrains.dukat.tsmodel.factory

import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.tsmodel.DefinitionInfoDeclaration
import org.jetbrains.dukat.tsmodel.EnumTokenDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.IdentifierDeclaration
import org.jetbrains.dukat.tsmodel.ImportEqualsDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ModuleReferenceDeclaration
import org.jetbrains.dukat.tsmodel.PackageDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration


interface AstNodeFactory<T> {

    fun createDefinitionInfoDeclaration(fileName: String): T

    fun createTupleDeclaration(params: List<ParameterValueDeclaration>): T

    fun createImportEqualsDeclaration(name: String, moduleReference: ModuleReferenceDeclaration, uid: String): ImportEqualsDeclaration

    fun createIdentifierDeclaration(value: String): T

    fun createQualifiedNameDeclaration(left: NameEntity, right: IdentifierDeclaration): T

    fun createThisTypeDeclaration(): T

    fun createEnumDeclaration(name: String, values: List<EnumTokenDeclaration>): T

    fun createEnumTokenDeclaration(value: String, meta: String): T

    fun createExportAssignmentDeclaration(name: String, isExportEquals: Boolean): T

    fun createHeritageClauseDeclaration(
            name: NameEntity,
            typeArguments: List<ParameterValueDeclaration>,
            extending: Boolean
    ): T

    fun createTypeAliasDeclaration(
            aliasName: NameEntity,
            typeParameters: List<IdentifierDeclaration>,
            typeReference: ParameterValueDeclaration,
            uid: String
    ): T

    fun createStringTypeDeclaration(tokens: List<String>): T;
    fun createIndexSignatureDeclaration(indexTypes: List<ParameterDeclaration>, returnType: ParameterValueDeclaration): T

    fun createCallSignatureDeclaration(
            parameters: List<ParameterDeclaration>,
            type: ParameterValueDeclaration,
            typeParameters: List<TypeParameterDeclaration>
    ): T

    fun createModifierDeclaration(token: String): T

    fun createClassDeclaration(
            name: String,
            members: List<MemberEntity>,
            typeParameters: List<TypeParameterDeclaration>,
            parentEntities: List<HeritageClauseDeclaration>,
            modifiers: List<ModifierDeclaration>,
            uid: String
    ): T

    fun createObjectLiteral(members: List<MemberEntity>): T

    fun createInterfaceDeclaration(
            name: String,
            members: List<MemberEntity>,
            typeParameters: List<TypeParameterDeclaration>,
            parentEntities: List<HeritageClauseDeclaration>,
            definitionsInfo: List<DefinitionInfoDeclaration>,
            uid: String
    ): T

    fun createExpression(kind: TypeDeclaration, meta: String?): T
    fun declareVariable(name: String, type: ParameterValueDeclaration, modifiers: List<ModifierDeclaration>, uid: String): T
    fun declareProperty(
            name: String,
            type: ParameterValueDeclaration,
            parameters: List<TypeParameterDeclaration>,
            optional: Boolean,
            modifiers: List<ModifierDeclaration>
    ): T

    fun createConstructorDeclaration(
            parameters: List<ParameterDeclaration>,
            type: ParameterValueDeclaration,
            typeParameters: List<TypeParameterDeclaration>,
            modifiers: List<ModifierDeclaration>
    ): T

    fun createFunctionDeclaration(
            name: String,
            parameters: Array<ParameterDeclaration>,
            type: ParameterValueDeclaration,
            typeParameters: Array<TypeParameterDeclaration>,
            modifiers: List<ModifierDeclaration>,
            uid: String
    ): T

    fun createMethodSignatureDeclaration(
            name: String,
            parameters: Array<ParameterDeclaration>,
            type: ParameterValueDeclaration,
            typeParameters: Array<TypeParameterDeclaration>,
            optional: Boolean,
            modifiers: List<ModifierDeclaration>
    ): T

    fun createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValueDeclaration): T
    fun createParameterDeclaration(
            name: String, type: ParameterValueDeclaration,
            initializer: ExpressionDeclaration?,
            vararg: Boolean,
            optional: Boolean
    ): T

    fun createIntersectionTypeDeclaration(params: List<ParameterValueDeclaration>): T
    fun createUnionTypeDeclaration(params: List<ParameterValueDeclaration>): T
    fun createTypeDeclaration(value: NameEntity, params: Array<ParameterValueDeclaration>): T
    fun createDocumentRoot(packageName: String, declarations: Array<TopLevelEntity>, modifiers: List<ModifierDeclaration>, definitionsInfo: List<DefinitionInfoDeclaration>, uid: String, resourceName: String, root: Boolean): T
    fun createSourceFileDeclaration(fileName: String, root: PackageDeclaration, referencedFiles: List<IdentifierDeclaration>): T
    fun createTypeParam(name: NameEntity, constraints: Array<ParameterValueDeclaration>): T

    fun createSourceSet(sources: List<SourceFileDeclaration>): T
}