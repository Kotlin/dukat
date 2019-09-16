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

        packageJsonOwner?.let { jsonOwner ->
            val jsonOwners = generateSequence(jsonOwner) { it.parentFile }
            val nodeModules = jsonOwners.find {
                println(it.name)
                it.name == "node_modules"
            }

            nodeModules?.let {
                val packageJsonFile = File(jsonOwner, "package.json")
                val packageJsonContent = packageJsonFile.readText()
                val packageJson = Json.nonstrict.parse(PackageJsonModel.serializer(), packageJsonContent)

                packageJson.name?.let { name ->
                    return name.removePrefix("@types/")
                }

                return jsonOwner.name
            }
        }

        return null;
    }

    override fun resolveName(sourceName: String): String? {
        return resolveName(File(sourceName))
    }
}