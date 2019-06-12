package org.jetbrains.dukat.moduleNameResolver

import org.jetbrains.dukat.astCommon.NameEntity

class ChainedNameResolver(val resolvers: List<ModuleNameResolver>) : ModuleNameResolver {
    override fun resolveName(sourceName: String): NameEntity? {

        resolvers.forEach { resolver ->
            val name = resolver.resolveName(sourceName)
            if (name != null) {
                return name
            }
        }

        return null
    }
}