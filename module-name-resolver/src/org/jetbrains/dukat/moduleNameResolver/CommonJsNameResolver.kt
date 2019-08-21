package org.jetbrains.dukat.moduleNameResolver

import kotlinx.serialization.json.Json
import java.io.File

class CommonJsNameResolver : ModuleNameResolver {

    @UseExperimental(kotlinx.serialization.UnstableDefault::class)
    fun resolveName(sourceFile: File): String? {
        val parentDirs = generateSequence(sourceFile.parentFile) { it.parentFile }

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
                return name.removePrefix("@types/")
            }
        }

        return null;
    }

    override fun resolveName(sourceName: String): String? {
        return resolveName(File(sourceName))
    }
}