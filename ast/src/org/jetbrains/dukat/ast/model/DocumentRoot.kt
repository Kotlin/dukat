package org.jetbrains.dukat.ast.model

data class DocumentRoot(
        val declarations: List<Declaration> = emptyList()
) : Declaration {
    constructor(declarations: Array<Declaration>) : this(declarations.toList())
}