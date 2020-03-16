package org.jetbrains.dukat.astModel.expressions

data class IndexExpressionModel(
    val array: ExpressionModel,
    val index: ExpressionModel
): ExpressionModel