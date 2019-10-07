package org.jetbrains.dukat.js.declarations

data class JSParameterDeclaration(
        val name: String,
        val vararg: Boolean
)