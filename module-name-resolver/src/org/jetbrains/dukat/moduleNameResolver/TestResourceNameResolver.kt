package org.jetbrains.dukat.moduleNameResolver

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import java.io.File

class TestResourceNameResolver : ModuleNameResolver {
    override fun resolveName(sourceName: String): NameEntity? {
        return IdentifierEntity(File(sourceName).parentFile.name)
    }
}