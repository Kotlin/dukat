package org.jetbrains.dukat.astModel.expressions

data class ParenthesizedExpressionModel(
    val expression: ExpressionModel
) : ExpressionModel