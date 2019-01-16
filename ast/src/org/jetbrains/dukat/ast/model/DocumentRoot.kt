package org.jetbrains.dukat.ast.model

data class DocumentRoot(
        val packageName: String,
        val declarations: List<Declaration> = emptyList()
) : Declaration