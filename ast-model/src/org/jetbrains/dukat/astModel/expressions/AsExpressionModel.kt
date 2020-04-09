package org.jetbrains.dukat.astModel.expressions

import org.jetbrains.dukat.astModel.TypeModel

data class AsExpressionModel(
    val expression: ExpressionModel,
    val type: TypeModel
) : ExpressionModel
