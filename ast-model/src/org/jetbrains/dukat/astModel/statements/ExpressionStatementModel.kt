package org.jetbrains.dukat.astModel.statements

import org.jetbrains.dukat.astModel.expressions.ExpressionModel

data class ExpressionStatementModel(
    val expression: ExpressionModel,
    override val metaDescription: String? = null
): StatementModel