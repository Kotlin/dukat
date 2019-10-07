package org.jetbrains.dukat.js.declarations

import org.jetbrains.dukat.astCommon.NameEntity

data class JSModuleDeclaration(
        val name: NameEntity,
        val fileName: String,
        val functions: List<JSFunctionDeclaration>
)
