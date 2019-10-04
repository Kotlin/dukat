package org.jetbrains.dukat.jsDeclarations

import org.jetbrains.dukat.astCommon.NameEntity

data class JSModuleDeclaration(
        val fileName: String,
        val name: NameEntity
)