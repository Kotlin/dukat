package org.jetbrains.dukat.astModel.expressions

import org.jetbrains.dukat.astModel.TypeModel

data class IsExpressionModel(
    val expression: ExpressionModel,
    val type: TypeModel
) : ExpressionModel