package org.jetbrains.dukat.astModel.statements

import org.jetbrains.dukat.astModel.expressions.ExpressionModel

data class IfStatementModel(
    val condition: ExpressionModel,
    val thenStatement: StatementModel,
    val elseStatement: StatementModel?,
    override val metaDescription: String? = null
): StatementModel