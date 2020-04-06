package org.jetbrains.dukat.astModel.expressions

data class UnaryExpressionModel(
    val operand: ExpressionModel,
    val operator: String,
    val isPrefix: Boolean
) : ExpressionModel