package org.jetbrains.dukat.js.declarations.member

import org.jetbrains.dukat.js.declarations.toplevel.JSDeclaration
import org.jetbrains.dukat.js.declarations.toplevel.JSFunctionDeclaration

data class JSMethodDeclaration(
        override val name: String,
        val function: JSFunctionDeclaration,

        val static: Boolean
) : JSDeclaration