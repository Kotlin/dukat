package org.jetbrains.dukat.astModel.expressions

data class BinaryExpressionModel(
    val left: ExpressionModel,
    val operator: String,
    val right: ExpressionModel
) : ExpressionModel