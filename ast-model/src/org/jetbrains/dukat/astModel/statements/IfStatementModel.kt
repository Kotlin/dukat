package org.jetbrains.dukat.astModel.statements

import org.jetbrains.dukat.astModel.expressions.ExpressionModel

data class IfStatementModel(
    val condition: ExpressionModel,
    val thenStatement: BlockStatementModel,
    val elseStatement: BlockStatementModel?,
    override val metaDescription: String? = null
): StatementModel