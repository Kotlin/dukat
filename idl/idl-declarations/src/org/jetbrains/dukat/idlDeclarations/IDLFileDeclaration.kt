package org.jetbrains.dukat.idlDeclarations

data class IDLFileDeclaration(
        val fileName: String,
        val declarations: List<IDLTopLevelDeclaration>
) : IDLDeclaration