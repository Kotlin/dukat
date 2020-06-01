package org.jetbrains.dukat.tsmodel.expression

data class ElementAccessExpressionDeclaration(
    val expression: ExpressionDeclaration,
    val argumentExpression: ExpressionDeclaration
) : ExpressionDeclaration