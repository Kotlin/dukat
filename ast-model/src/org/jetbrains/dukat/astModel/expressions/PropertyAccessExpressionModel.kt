package org.jetbrains.dukat.astModel.expressions

data class PropertyAccessExpressionModel(
    val left: ExpressionModel,
    val right: ExpressionModel
): ExpressionModel