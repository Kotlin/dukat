package org.jetbrains.dukat.js.declarations.toplevel

data class JSClassDeclaration(
        override val name: String,
        val methods: List<JSFunctionDeclaration>
) : JSTopLevelDeclaration