package org.jetbrains.dukat.tsmodel

data class ExpressionStatementDeclaration(
        val expression: ExpressionDeclaration
) : StatementDeclaration