package org.jetbrains.dukat.js.declarations.toplevel

import org.jetbrains.dukat.js.declarations.JSDeclaration
import org.jetbrains.dukat.js.declarations.JSScopedDeclaration

data class JSClassDeclaration(
        val name: String,
        override val scopeDeclarations: MutableMap<String, JSDeclaration>
) : JSScopedDeclaration, JSDeclaration