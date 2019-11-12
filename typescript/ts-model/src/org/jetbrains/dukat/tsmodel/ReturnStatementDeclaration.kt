package org.jetbrains.dukat.tsmodel

data class ReturnStatementDeclaration(
        val expression: ExpressionDeclaration?
) : TopLevelDeclaration