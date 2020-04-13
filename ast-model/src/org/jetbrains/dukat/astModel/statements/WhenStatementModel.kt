package org.jetbrains.dukat.astModel.statements

import org.jetbrains.dukat.astModel.expressions.ExpressionModel

data class WhenStatementModel(
    val expression: ExpressionModel,
    val cases: List<CaseModel>,
    override val metaDescription: String? = null
) : StatementModel