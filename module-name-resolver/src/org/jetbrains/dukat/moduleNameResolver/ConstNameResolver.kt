package org.jetbrains.dukat.moduleNameResolver

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity

class ConstNameResolver : ModuleNameResolver {
    override fun resolveName(sourceName: String): NameEntity? {
        return IdentifierEntity("<RESOLVED_MODULE_NAME>")
    }
}