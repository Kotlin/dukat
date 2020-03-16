package org.jetbrains.dukat.tsmodel

data class IfStatementDeclaration(
        val condition: ExpressionDeclaration,
        val thenStatement: StatementDeclaration,
        val elseStatement: StatementDeclaration?
) : StatementDeclaration
