package org.jetbrains.dukat.moduleNameResolver

interface ModuleNameResolver {
    fun resolveName(sourceName: String): String?
}