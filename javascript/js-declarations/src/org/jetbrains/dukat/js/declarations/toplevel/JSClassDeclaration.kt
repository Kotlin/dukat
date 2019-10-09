package org.jetbrains.dukat.js.declarations.toplevel

import org.jetbrains.dukat.js.declarations.JSScopedDeclaration

data class JSClassDeclaration(
        override val name: String,
        override val scopeDeclarations: MutableMap<String, JSDeclaration>
) : JSScopedDeclaration, JSDeclaration