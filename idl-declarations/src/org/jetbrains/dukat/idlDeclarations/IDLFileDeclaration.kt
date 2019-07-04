package org.jetbrains.dukat.idlDeclarations

import org.jetbrains.dukat.astCommon.TopLevelEntity

class IDLFileDeclaration(
        val fileName: String,
        val declarations: List<IDLDeclaration>
) : IDLDeclaration