package org.jetbrains.dukat.moduleNameResolver

import org.jetbrains.dukat.astCommon.NameEntity

class NullNameResolver : ModuleNameResolver {
    override fun resolveName(sourceName: String): NameEntity? {
        return null
    }
}