package org.jetbrains.dukat.tsmodel

data class ThrowStatementDeclaration(
        val expression: ExpressionDeclaration?
) : TopLevelDeclaration