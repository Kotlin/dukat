package org.jetbrains.dukat.ast.factory

import org.jetbrains.dukat.ast.model.declaration.ClassLikeDeclaration
import org.jetbrains.dukat.ast.model.declaration.Declaration
import org.jetbrains.dukat.ast.model.declaration.ExpressionDeclaration
import org.jetbrains.dukat.ast.model.declaration.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.declaration.MemberDeclaration
import org.jetbrains.dukat.ast.model.declaration.ModifierDeclaration
import org.jetbrains.dukat.ast.model.declaration.ParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.TypeParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration

interface AstNodeFactory<T> {
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
            parentEntities: List<ClassLikeDeclaration>,
            staticMembers: List<MemberDeclaration>
    ): T

    fun createObjectLiteral(members: List<MemberDeclaration>): T

    fun createInterfaceDeclaration(
            name: String,
            members: List<MemberDeclaration>,
            typeParameters: List<TypeParameterDeclaration>,
            parentEntities: List<InterfaceDeclaration>
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
    fun createParameterDeclaration(name: String, type: ParameterValueDeclaration, initializer: ExpressionDeclaration?): T
    fun createTypeDeclaration(value: String, params: Array<ParameterValueDeclaration>): T
    fun createDocumentRoot(packageName: String, declarations: Array<Declaration>): T
    fun createTypeParam(name: String, constraints: Array<ParameterValueDeclaration>): T
}