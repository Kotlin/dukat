package org.jetbrains.dukat.ast.model.declaration

data class DocumentRootDeclaration(
        val packageName: String,
        val declarations: List<Declaration> = emptyList()
) : Declaration