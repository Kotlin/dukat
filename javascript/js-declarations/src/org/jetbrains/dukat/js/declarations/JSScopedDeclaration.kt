package org.jetbrains.dukat.js.declarations

import org.jetbrains.dukat.js.declarations.toplevel.JSDeclaration

interface JSScopedDeclaration {
    val scopeDeclarations: MutableMap<String, JSDeclaration>
}