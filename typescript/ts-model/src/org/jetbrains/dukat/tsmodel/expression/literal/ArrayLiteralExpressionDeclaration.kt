package org.jetbrains.dukat.tsmodel.expression.literal

import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration

data class ArrayLiteralExpressionDeclaration(
        val elements: List<ExpressionDeclaration>
) : LiteralExpressionDeclaration