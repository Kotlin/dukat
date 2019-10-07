package org.jetbrains.dukat.js.declarations

import org.jetbrains.dukat.astCommon.NameEntity

data class JSModuleDeclaration(
        val fileName: String,
        val name: NameEntity
)