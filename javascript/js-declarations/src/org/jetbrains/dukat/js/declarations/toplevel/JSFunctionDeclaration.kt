package org.jetbrains.dukat.js.declarations.toplevel

import org.jetbrains.dukat.js.declarations.misc.JSParameterDeclaration

data class JSFunctionDeclaration(
        override val name: String,
        val parameters: List<JSParameterDeclaration>
) : JSTopLevelDeclaration