package org.jetbrains.dukat.astModel.expressions

import org.jetbrains.dukat.astModel.expressions.operators.UnaryOperatorModel

data class UnaryExpressionModel(
    val operand: ExpressionModel,
    val operator: UnaryOperatorModel,
    val isPrefix: Boolean
) : ExpressionModel