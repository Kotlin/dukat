package org.jetbrains.dukat.moduleNameResolver

class ConstNameResolver(private val name: String = "<RESOLVED_MODULE_NAME>") : ModuleNameResolver {
    override fun resolveName(sourceName: String): String? {
        return name
    }
}