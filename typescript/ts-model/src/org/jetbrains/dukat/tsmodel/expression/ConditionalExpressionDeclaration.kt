package org.jetbrains.dukat.tsmodel.expression

data class ConditionalExpressionDeclaration(
    val condition: ExpressionDeclaration,
    val whenTrue: ExpressionDeclaration,
    val whenFalse: ExpressionDeclaration
) : ExpressionDeclaration