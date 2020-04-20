package org.jetbrains.dukat.tsmodel.expression

data class UnaryExpressionDeclaration(
    val operand: ExpressionDeclaration,
    val operator: String,
    val isPrefix: Boolean
) : ExpressionDeclaration