package org.jetbrains.dukat.tsmodel.expression

class ElementAccessExpressionDeclaration(
    val expression: ExpressionDeclaration,
    val argumentExpression: ExpressionDeclaration
) : ExpressionDeclaration