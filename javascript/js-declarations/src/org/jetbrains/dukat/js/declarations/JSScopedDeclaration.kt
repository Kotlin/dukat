package org.jetbrains.dukat.js.declarations

interface JSScopedDeclaration {
    val scopeDeclarations: MutableMap<String, JSDeclaration>
}