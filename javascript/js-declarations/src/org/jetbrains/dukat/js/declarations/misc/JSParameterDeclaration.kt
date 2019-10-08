package org.jetbrains.dukat.js.declarations.misc

data class JSParameterDeclaration(
        val name: String,
        val vararg: Boolean
)