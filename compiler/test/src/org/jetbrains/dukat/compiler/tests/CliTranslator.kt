package org.jetbrains.dukat.compiler.tests

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.dukat.compiler.tests.core.TestConfig
import java.io.File
import java.util.concurrent.TimeUnit

@Serializable
internal data class EnvJson(val NODE: String)

class CliTranslator(val envDataPath: String, private val translatorPath: String) {
    private val nodePath: String

    init {
        val envJson = Json.nonstrict.parse(EnvJson.serializer(), File(envDataPath).readText())
        nodePath = envJson.NODE
    }

    fun translate(input: String, dirName: String, reportPath: String? = null) {
        val proc =
                if (reportPath == null) {
                    ProcessBuilder(nodePath, translatorPath, "-d", dirName, input).start()
                } else {
                    ProcessBuilder(nodePath, translatorPath, "-d", dirName, "-r", reportPath, input).start()
                }
        proc.waitFor(TestConfig.COMPILATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)

        println(proc.inputStream.bufferedReader().readText())

        if (proc.exitValue() > 0) {
            println("exited with value ${proc.exitValue()}")
            println(proc.errorStream.bufferedReader().readText())
        }
    }
}