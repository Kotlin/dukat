package org.jetbrains.dukat.js.declarations.toplevel

import org.jetbrains.dukat.js.declarations.JSDeclaration
import org.jetbrains.dukat.js.declarations.misc.JSParameterDeclaration

data class JSFunctionDeclaration(
        val name: String,
        val parameters: List<JSParameterDeclaration>
) : JSDeclaration