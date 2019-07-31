package org.jetbrains.dukat.idlDeclarations

import org.jetbrains.dukat.astCommon.NameEntity

data class IDLFileDeclaration(
        val fileName: String,
        val declarations: List<IDLTopLevelDeclaration>,
        val referencedFiles: List<String>,
        val packageName: NameEntity?
) : IDLDeclaration