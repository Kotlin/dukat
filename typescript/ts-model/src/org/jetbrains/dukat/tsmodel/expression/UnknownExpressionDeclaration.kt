package org.jetbrains.dukat.tsmodel.expression

import org.jetbrains.dukat.tsmodel.ExpressionDeclaration

//TODO convert all expressions, so that this is no longer necessary
data class UnknownExpressionDeclaration(
        val meta: String
) : ExpressionDeclaration