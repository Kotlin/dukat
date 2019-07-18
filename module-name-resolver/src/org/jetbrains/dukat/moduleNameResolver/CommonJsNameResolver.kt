package org.jetbrains.dukat.moduleNameResolver

import kotlinx.serialization.json.Json
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import java.io.File

class CommonJsNameResolver : ModuleNameResolver {
    fun resolveName(sourceFile: File): NameEntity? {
        val parentDirs = generateSequence(File(sourceFile.parent)) { if (it.parent != null) File(it.parent) else null }

        val packageJsonOwner = parentDirs.find { parentDir ->
            File(parentDir, "package.json").exists()
        }

        val packageJsonOwnerOwner = packageJsonOwner?.let { File(it.parent) }
        val isNodeModulesDir = packageJsonOwnerOwner?.name == "node_modules"
        val isTypesDir = packageJsonOwnerOwner?.name == "@types" && packageJsonOwnerOwner.parentFile?.name == "node_modules"

        if (isNodeModulesDir || isTypesDir) {
            val packageJsonFile = File(packageJsonOwner, "package.json")
            val packageJsonContent = packageJsonFile.readText()
            val packageJson = Json.nonstrict.parse(PackageJsonModel.serializer(), packageJsonContent)

            packageJson.name?.let { name ->
                return IdentifierEntity(name.removePrefix("@types/"))
            }
        }

        return null;
    }

    override fun resolveName(sourceName: String): NameEntity? {
        return resolveName(File(sourceName))
    }
}