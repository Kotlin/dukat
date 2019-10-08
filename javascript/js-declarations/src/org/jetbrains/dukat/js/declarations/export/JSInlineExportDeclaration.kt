package org.jetbrains.dukat.js.declarations.export

import org.jetbrains.dukat.js.declarations.toplevel.JSTopLevelDeclaration

data class JSInlineExportDeclaration(
        val name: String,
        val declaration: JSTopLevelDeclaration
) : JSExportDeclaration