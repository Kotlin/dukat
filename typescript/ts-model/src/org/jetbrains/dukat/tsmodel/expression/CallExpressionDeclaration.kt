package org.jetbrains.dukat.tsmodel.expression

data class CallExpressionDeclaration(
    val expression: ExpressionDeclaration,
    val arguments: List<ExpressionDeclaration>
) : ExpressionDeclaration