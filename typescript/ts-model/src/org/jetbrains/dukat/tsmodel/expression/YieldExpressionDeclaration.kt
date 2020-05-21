package org.jetbrains.dukat.tsmodel.expression

data class YieldExpressionDeclaration(
    val expression: ExpressionDeclaration?,
    val hasAsterisk: Boolean
) : ExpressionDeclaration
