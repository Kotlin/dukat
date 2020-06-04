package org.jetbrains.dukat.astModel.expressions

import org.jetbrains.dukat.astModel.TypeModel

data class CallExpressionModel(
    val expression: ExpressionModel,
    val arguments: List<ExpressionModel>,
    val typeParameters: List<TypeModel> = listOf()
) : ExpressionModel