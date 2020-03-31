package org.jetbrains.dukat.moduleNameResolver

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File

class CommonJsNameResolver : ModuleNameResolver {

    fun resolveName(sourceFile: File): String? {
        val parentDirs = generateSequence(sourceFile.parentFile) { it.parentFile }

        val packageJsonOwner = parentDirs.find { parentDir ->
            File(parentDir, "package.json").exists()
        }

        return packageJsonOwner?.let { jsonOwner ->
            val packageJsonFile = File(jsonOwner, "package.json")
            val packageJsonContent = packageJsonFile.readText()
            val packageJson = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true)).parse(PackageJsonModel.serializer(), packageJsonContent)

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