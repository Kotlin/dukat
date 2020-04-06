package org.jetbrains.dukat.astModel.expressions

data class ConditionalExpressionModel(
    val condition: ExpressionModel,
    val whenTrue: ExpressionModel,
    val whenFalse: ExpressionModel
) : ExpressionModel
