package org.jetbrains.dukat.tsmodel.expression

data class NonNullExpressionDeclaration(
    val expression: ExpressionDeclaration
): ExpressionDeclaration
