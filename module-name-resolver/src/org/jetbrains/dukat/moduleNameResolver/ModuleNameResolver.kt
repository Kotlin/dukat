package org.jetbrains.dukat.moduleNameResolver

import org.jetbrains.dukat.astCommon.NameEntity

interface ModuleNameResolver {
    fun resolveName(sourceName: String): NameEntity?
}