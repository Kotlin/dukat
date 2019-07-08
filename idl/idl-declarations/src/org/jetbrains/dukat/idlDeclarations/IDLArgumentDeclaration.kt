package org.jetbrains.dukat.idlDeclarations

data class IDLArgumentDeclaration(
        val name: String,
        val type: IDLTypeDeclaration
): IDLDeclaration