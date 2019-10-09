package org.jetbrains.dukat.js.declarations

import org.jetbrains.dukat.js.declarations.toplevel.JSDeclaration

data class JSModuleDeclaration(
        val moduleName: String,
        val fileName: String,
        val exportDeclarations: MutableSet<JSDeclaration>,
        override val scopeDeclarations: MutableMap<String, JSDeclaration>
) : JSScopedDeclaration
