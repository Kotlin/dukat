package org.jetbrains.dukat.compiler.tests

import kotlinx.serialization.Serializable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File

@Serializable
internal data class EnvJson(val NODE: String)

@UnstableDefault
class NodeResolver(envDataPath: String) {
    val nodePath: String

    init {
        val envJson = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true)).parse(EnvJson.serializer(), File(envDataPath).readText())
        nodePath = envJson.NODE
    }
}