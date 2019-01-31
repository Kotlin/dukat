package org.jetbrains.dukat.tsmodel.factory

import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TokenDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration


interface AstNodeFactory<T> {
    fun createTokenDeclaration(value: String): T

    fun createHeritageClauseDeclaration(
            name: String,
            typeArguments: List<TokenDeclaration>,
            extending: Boolean
    ): T

    fun createTypeAliasDeclaration(
            aliasName: String,
            typeParameters: List<TokenDeclaration>,
            typeReference: ParameterValueDeclaration
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
            members: List<MemberDeclaration>,
            typeParameters: List<TypeParameterDeclaration>,
            parentEntities: List<HeritageClauseDeclaration>,
            modifiers: List<ModifierDeclaration>
    ): T

    fun createObjectLiteral(members: List<MemberDeclaration>): T

    fun createInterfaceDeclaration(
            name: String,
            members: List<MemberDeclaration>,
            typeParameters: List<TypeParameterDeclaration>,
            parentEntities: List<HeritageClauseDeclaration>
    ): T

    fun createExpression(kind: TypeDeclaration, meta: String?): T
    fun declareVariable(name: String, type: ParameterValueDeclaration): T
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
            modifiers: List<ModifierDeclaration>
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

    fun createUnionDeclaration(params: List<ParameterValueDeclaration>): T
    fun createTypeDeclaration(value: String, params: Array<ParameterValueDeclaration>): T
    fun createDocumentRoot(packageName: String, declarations: Array<TopLevelDeclaration>): T
    fun createTypeParam(name: String, constraints: Array<ParameterValueDeclaration>): T
}