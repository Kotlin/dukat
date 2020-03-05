package org.jetbrains.dukat.tsmodel

data class WhileStatementDeclaration(
        val condition: ExpressionDeclaration,
        val statement: StatementDeclaration
) : StatementDeclaration
