package org.jetbrains.dukat.tsmodel.expression

import org.jetbrains.dukat.tsmodel.ExpressionDeclaration

data class UnaryExpressionDeclaration(
        val operand: ExpressionDeclaration,
        val operator: String,
        val isPrefix: Boolean
) : ExpressionDeclaration