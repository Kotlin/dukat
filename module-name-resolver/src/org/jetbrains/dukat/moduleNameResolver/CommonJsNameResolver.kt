package org.jetbrains.dukat.moduleNameResolver

import kotlinx.serialization.json.Json
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import java.io.File

class CommonJsNameResolver : ModuleNameResolver {
    override fun resolveName(sourceName: String): NameEntity? {
        val file = File(sourceName)

        val parentDirs = generateSequence(File(file.parent)) { if (it.parent != null) File(it.parent) else null }

        val packageJsonOwner = parentDirs.find { parentDir ->
            File(parentDir, "package.json").exists()
        }

        val packageJsonOwnerOwner = packageJsonOwner?.let { File(it.parent) }
        if (packageJsonOwnerOwner?.name == "node_modules") {
            val packageJsonFile = File(packageJsonOwner, "package.json")
            val packageJsonContent = packageJsonFile.readText()
            val packageJson = Json.nonstrict.parse(PackageJsonModel.serializer(), packageJsonContent)

            packageJson.name?.let { name ->
                return IdentifierEntity(name.removePrefix("@types/"))
            }
        }

        return null;
    }
}