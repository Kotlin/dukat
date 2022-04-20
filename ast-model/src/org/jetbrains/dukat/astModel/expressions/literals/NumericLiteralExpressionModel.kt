package org.jetbrains.dukat.astModel.expressions.literals

data class NumericLiteralExpressionModel(
    val value: Number
): LiteralExpressionModel
