package org.jetbrains.dukat.tsmodel.expression

data class NewExpressionDeclaration(
    val expression: ExpressionDeclaration,
    val arguments: List<ExpressionDeclaration>
) : ExpressionDeclaration