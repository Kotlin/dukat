package org.jetbrains.dukat.tsmodel

data class IfStatementDeclaration(
        val condition: ExpressionDeclaration,
        val thenStatement: BlockDeclaration,
        val elseStatement: BlockDeclaration?
) : StatementDeclaration
