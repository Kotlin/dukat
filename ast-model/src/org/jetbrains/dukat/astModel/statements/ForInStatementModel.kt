package org.jetbrains.dukat.astModel.statements

import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.expressions.ExpressionModel

data class ForInStatementModel(
    val variable: VariableModel,
    val expression: ExpressionModel,
    val body: BlockStatementModel,
    override val metaDescription: String? = null
) : StatementModel