package org.jetbrains.dukat.tsmodel.expression

import org.jetbrains.dukat.tsmodel.ExpressionDeclaration

data class ConditionalExpressionDeclaration(
        val condition: ExpressionDeclaration,
        val whenTrue: ExpressionDeclaration,
        val whenFalse: ExpressionDeclaration
) : ExpressionDeclaration