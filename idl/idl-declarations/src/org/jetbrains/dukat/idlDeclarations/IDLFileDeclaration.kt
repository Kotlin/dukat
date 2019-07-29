package org.jetbrains.dukat.idlDeclarations

data class IDLFileDeclaration(
        val fileName: String,
        val declarations: List<IDLTopLevelDeclaration>,
        val referencedFiles: List<String>
) : IDLDeclaration