package org.jetbrains.dukat.idlDeclarations

data class IDLSourceSetDeclaration(
        val files: List<IDLFileDeclaration>
) : IDLDeclaration