package org.jetbrains.dukat.tsmodel.expression

import org.jetbrains.dukat.tsmodel.ExpressionDeclaration

data class CallExpressionDeclaration(
        val expression: ExpressionDeclaration,
        val arguments: List<ExpressionDeclaration>
) : ExpressionDeclaration