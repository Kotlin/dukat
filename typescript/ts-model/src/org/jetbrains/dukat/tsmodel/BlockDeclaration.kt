package org.jetbrains.dukat.tsmodel

data class BlockDeclaration(
        val statements: List<StatementDeclaration>
) : StatementDeclaration