package org.jetbrains.dukat.astModel.expressions

data class NonNullExpressionModel(
    val expression: ExpressionModel
) : ExpressionModel
