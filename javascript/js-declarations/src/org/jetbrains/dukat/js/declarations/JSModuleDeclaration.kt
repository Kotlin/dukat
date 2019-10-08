package org.jetbrains.dukat.js.declarations

import org.jetbrains.dukat.js.declarations.export.JSExportDeclaration
import org.jetbrains.dukat.js.declarations.toplevel.JSTopLevelDeclaration

data class JSModuleDeclaration(
        val moduleName: String,
        val fileName: String,
        val exportDeclarations: List<JSExportDeclaration>,
        val topLevelDeclarations: Map<String, JSTopLevelDeclaration>
)
