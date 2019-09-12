package org.jetbrains.dukat.compiler.tests

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.TimeUnit

@Serializable
internal data class EnvJson(val NODE: String)

class CliTranslator(private val envDataPath: String, private val translatorPath: String) {
    private val nodePath: String

    init {
        val envJson = Json.nonstrict.parse(EnvJson.serializer(), File(envDataPath).readText())
        nodePath = envJson.NODE
    }

    fun translate(input: String, dirName: String) {
        val proc = ProcessBuilder(nodePath, translatorPath, "-d", dirName, input).start()
        proc.waitFor(180, TimeUnit.SECONDS)

        if (proc.exitValue() > 0) {
            println("exited with value ${proc.exitValue()}")
            println(proc.errorStream.bufferedReader().readText())
        }
    }
}