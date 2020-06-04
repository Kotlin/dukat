package org.jetbrains.dukat.tsmodel.expression

data class ParenthesizedExpressionDeclaration(
    val expression: ExpressionDeclaration
): ExpressionDeclaration