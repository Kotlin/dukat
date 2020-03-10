package org.jetbrains.dukat.astModel.statements

import org.jetbrains.dukat.astModel.expressions.ExpressionModel

data class AssignmentStatementModel(
        val left: ExpressionModel,
        val right: ExpressionModel,
        override val metaDescription: String? = null
) : StatementModel