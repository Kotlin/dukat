package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class MethodDeclaration(
        override val name: String,
        override val parameters: List<ParameterDeclaration>,
        override val type: ParameterValueDeclaration,
        override val typeParameters: List<TypeParameterDeclaration>,
        override val modifiers: Set<ModifierDeclaration>,
        val body: BlockDeclaration?,
        val optional: Boolean,
        val isGenerator: Boolean
) : StatementDeclaration, WithModifiersDeclaration,
        ExpressionDeclaration, FunctionLikeDeclaration, CallableMemberDeclaration, NamedMemberDeclaration, MemberDeclaration
