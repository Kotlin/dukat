package org.jetbrains.dukat.tsmodel.expression

//TODO convert all expressions, so that this is no longer necessary
data class UnknownExpressionDeclaration(
        val meta: String
) : ExpressionDeclaration