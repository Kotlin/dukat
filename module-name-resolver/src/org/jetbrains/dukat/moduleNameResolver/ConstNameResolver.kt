package org.jetbrains.dukat.moduleNameResolver

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity

class ConstNameResolver(private val name: NameEntity = IdentifierEntity("<RESOLVED_MODULE_NAME>")) : ModuleNameResolver {
    override fun resolveName(sourceName: String): NameEntity? {
        return name
    }
}