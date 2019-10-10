package org.jetbrains.dukat.js.declarations

data class JSModuleDeclaration(
        val moduleName: String,
        val fileName: String,
        var exportDeclaration: JSDeclaration?,
        override val scopeDeclarations: MutableMap<String, JSDeclaration>
) : JSScopedDeclaration
