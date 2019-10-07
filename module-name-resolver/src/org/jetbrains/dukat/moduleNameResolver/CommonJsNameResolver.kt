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

        return packageJsonOwner?.let { jsonOwner ->
            val packageJsonFile = File(jsonOwner, "package.json")
            val packageJsonContent = packageJsonFile.readText()
            val packageJson = Json.nonstrict.parse(PackageJsonModel.serializer(), packageJsonContent)

            if (packageJson.name == null) {
                jsonOwner.name
            } else {
                packageJson.name.removePrefix("@types/")
            }
        }
    }

    override fun resolveName(sourceName: String): String? {
        return resolveName(File(sourceName))
    }
}