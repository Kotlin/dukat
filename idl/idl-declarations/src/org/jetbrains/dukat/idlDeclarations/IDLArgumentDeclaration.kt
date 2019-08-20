package org.jetbrains.dukat.idlDeclarations

data class IDLArgumentDeclaration(
        val name: String,
        val type: IDLTypeDeclaration,
        val optional: Boolean,
        val variadic: Boolean
): IDLDeclaration