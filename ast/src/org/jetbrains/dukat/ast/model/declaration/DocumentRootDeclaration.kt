package org.jetbrains.dukat.ast.model.declaration

import org.jetbrains.dukat.ast.model.declaration.types.TopLevelDeclaration

data class DocumentRootDeclaration(
        val packageName: String,
        val declarations: List<TopLevelDeclaration> = emptyList()
) : TopLevelDeclaration