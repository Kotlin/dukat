package org.jetbrains.dukat.ast.model

data class DocumentRoot(
        val packageName: String,
        val declarations: List<Declaration> = emptyList()
) : Declaration {
    constructor(packageName: String, declarations: Array<Declaration>) : this(packageName, declarations.toList())
}