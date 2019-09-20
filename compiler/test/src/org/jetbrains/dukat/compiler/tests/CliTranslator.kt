package org.jetbrains.dukat.compiler.tests

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.dukat.compiler.tests.core.TestConfig
import org.jetbrains.kotlin.backend.common.push
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

    fun translate(
            input: String,
            dirName: String,
            reportPath: String? = null,
            moduleName: String? = null
            ) {

        val args = mutableListOf(
                nodePath,
                translatorPath,
                "-d", dirName
        )

        if (reportPath != null) {
            args.push("-r")
            args.push(reportPath)
        }

        if (moduleName != null) {
            args.push("-m")
            args.push(moduleName)
        }

        args.push(input)

        val proc= ProcessBuilder(*args.toTypedArray()).start()

        proc.waitFor(TestConfig.COMPILATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)

        println(proc.inputStream.bufferedReader().readText())

        if (proc.exitValue() > 0) {
            println("exited with value ${proc.exitValue()}")
            println(proc.errorStream.bufferedReader().readText())
        }
    }
}


fun createStandardCliTranslator(): CliTranslator {
    return CliTranslator(
            "../node-package/build/env.json",
            "../node-package/build/distrib/bin/dukat-cli.js"
    )
}