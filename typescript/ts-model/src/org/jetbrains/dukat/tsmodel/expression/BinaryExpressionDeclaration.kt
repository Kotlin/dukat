package org.jetbrains.dukat.tsmodel.expression

import org.jetbrains.dukat.tsmodel.ExpressionDeclaration

data class BinaryExpressionDeclaration(
        val left: ExpressionDeclaration,
        val operator: String,
        val right: ExpressionDeclaration
) : ExpressionDeclaration