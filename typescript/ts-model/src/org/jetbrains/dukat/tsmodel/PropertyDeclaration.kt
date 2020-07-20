package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class PropertyDeclaration(
    override val name: String,
    val initializer: ExpressionDeclaration? = null,
    val type: ParameterValueDeclaration,
    val typeParameters: List<TypeParameterDeclaration>,
    val optional: Boolean,
    override val modifiers: Set<ModifierDeclaration>,
    val explicitlyDeclaredType: Boolean
) : NamedMemberDeclaration, WithModifiersDeclaration