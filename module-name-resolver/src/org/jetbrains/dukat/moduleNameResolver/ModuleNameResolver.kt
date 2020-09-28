package org.jetbrains.dukat.moduleNameResolver

import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclarationKind

private fun String.unquote(): String {
    return replace("(?:^[\"\'])|(?:[\"\']$)".toRegex(), "")
}

interface ModuleNameResolver {
    fun resolveName(sourceName: String): String?
    fun resolveName(moduleDeclaration: ModuleDeclaration): String? {
        val name = moduleDeclaration.name
        return if (moduleDeclaration.kind == ModuleDeclarationKind.AMBIENT_MODULE) {
            if (name.startsWith("/")) {
                resolveName(name)
            } else {
                name.unquote()
            }
        } else {
            resolveName(moduleDeclaration.sourceName)
        }
    }
}