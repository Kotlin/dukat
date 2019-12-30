package org.jetbrains.dukat.tsmodel

data class BlockDeclaration(
        val statements: List<TopLevelDeclaration>
) : TopLevelDeclaration