package org.jetbrains.dukat.js.declarations

import org.jetbrains.dukat.astCommon.IdentifierEntity

data class JSParameterDeclaration(
        val name: String,
        val vararg: Boolean
)