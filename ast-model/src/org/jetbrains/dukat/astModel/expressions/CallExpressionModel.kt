package org.jetbrains.dukat.astModel.expressions

import org.jetbrains.dukat.astCommon.IdentifierEntity

data class CallExpressionModel(
    val expression: ExpressionModel,
    val arguments: List<ExpressionModel>,
    val typeParameters: List<IdentifierEntity> = listOf()
) : ExpressionModel