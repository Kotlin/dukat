package org.jetbrains.dukat.tsmodel.expression

import org.jetbrains.dukat.tsmodel.ExpressionDeclaration

data class TypeOfExpressionDeclaration(
        val expression: ExpressionDeclaration
) : ExpressionDeclaration