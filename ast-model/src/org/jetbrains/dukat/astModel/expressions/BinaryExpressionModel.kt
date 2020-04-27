package org.jetbrains.dukat.astModel.expressions

import org.jetbrains.dukat.astModel.expressions.operators.BinaryOperatorModel

data class BinaryExpressionModel(
    val left: ExpressionModel,
    val operator: BinaryOperatorModel,
    val right: ExpressionModel
) : ExpressionModel