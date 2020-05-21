package org.jetbrains.dukat.astModel.expressions

import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.statements.BlockStatementModel

data class LambdaExpressionModel(
    val parameters: List<ParameterModel>,
    val body: BlockStatementModel
) : ExpressionModel