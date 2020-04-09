package org.jetbrains.dukat.tsmodel.expression

data class BinaryExpressionDeclaration(
    val left: ExpressionDeclaration,
    val operator: String,
    val right: ExpressionDeclaration
) : ExpressionDeclaration