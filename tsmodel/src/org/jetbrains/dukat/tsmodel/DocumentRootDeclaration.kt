package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.types.TopLevelDeclaration

data class DocumentRootDeclaration(
        val packageName: String,
        val declarations: List<TopLevelDeclaration> = emptyList()
) : TopLevelDeclaration