package org.jetbrains.dukat.astModel.expressions

import org.jetbrains.dukat.astCommon.NameEntity

data class IdentifierExpressionModel(
    val identifier: NameEntity
) : ExpressionModel