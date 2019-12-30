package org.jetbrains.dukat.tsmodel.expression

import org.jetbrains.dukat.tsmodel.ExpressionDeclaration

data class NewExpressionDeclaration(
        val expression: ExpressionDeclaration,
        val arguments: List<ExpressionDeclaration>
) : ExpressionDeclaration