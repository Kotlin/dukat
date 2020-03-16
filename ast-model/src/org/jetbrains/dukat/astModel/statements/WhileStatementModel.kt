package org.jetbrains.dukat.astModel.statements

import org.jetbrains.dukat.astModel.expressions.ExpressionModel

data class WhileStatementModel(
    val condition: ExpressionModel,
    val body: StatementModel,
    override val metaDescription: String? = null
): StatementModel