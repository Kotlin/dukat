package org.jetbrains.dukat.tsmodel

data class IfStatementDeclaration(
        val condition: ExpressionDeclaration,
        val thenStatement: TopLevelDeclaration,
        val elseStatement: TopLevelDeclaration?
) : TopLevelDeclaration
