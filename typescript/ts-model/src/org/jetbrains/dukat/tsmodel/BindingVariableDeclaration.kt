package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration

data class BindingVariableDeclaration(
    val name: String,
    val initializer: ExpressionDeclaration?
) : BindingElementDeclaration