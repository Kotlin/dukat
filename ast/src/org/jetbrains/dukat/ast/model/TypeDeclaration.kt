package org.jetbrains.dukat.ast.model

data class TypeDeclaration(
        val value: String,
        val params: List<TypeDeclaration>
) : Declaration {
    constructor(value: String, params: Array<TypeDeclaration>) : this(value, params.toList())
}