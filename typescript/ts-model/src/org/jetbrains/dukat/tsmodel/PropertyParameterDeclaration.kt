package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class PropertyParameterDeclaration(
    override val name: String,
    override val type: ParameterValueDeclaration,
    override val initializer: ExpressionDeclaration?,
    val modifiers: Set<ModifierDeclaration>
): ConstructorParameterDeclaration