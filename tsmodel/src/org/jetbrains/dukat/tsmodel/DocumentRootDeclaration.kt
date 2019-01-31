package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.TopLevelDeclaration

data class DocumentRootDeclaration(
        val packageName: String,
        val declarations: List<TopLevelDeclaration> = emptyList()
) : TopLevelDeclaration