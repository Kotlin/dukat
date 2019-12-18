package org.jetbrains.dukat.compiler.tests

import kotlinx.serialization.Serializable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import org.jetbrains.dukat.cli.translateBinaryBundle
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.kotlin.backend.common.push
import java.io.File

@Serializable
internal data class EnvJson(val NODE: String)

@UseExperimental(UnstableDefault::class)
class CliTranslator(val envDataPath: String, private val translatorPath: String) {
    private val nodePath: String

    init {
        val envJson = Json.nonstrict.parse(EnvJson.serializer(), File(envDataPath).readText())
        nodePath = envJson.NODE
    }

    private fun createCliArgs(
            input: String,
            binaryOutput: Boolean = false,
            dirName: String? = null,
            reportPath: String? = null,
            moduleName: String? = null
    ): Array<String> {
        val args = mutableListOf(
                nodePath,
                translatorPath
        )

        if (dirName != null) {
            args.push("-d")
            args.push(dirName)
        }

        if (reportPath != null) {
            args.push("-r")
            args.push(reportPath)
        }

        if (moduleName != null) {
            args.push("-m")
            args.push(moduleName)
        }

        if (binaryOutput) {
            args.push("-b")
        }

        args.push(input)
        return args.toTypedArray()
    }

    fun translate(
            input: String,
            dirName: String,
            reportPath: String? = null,
            moduleName: String? = null
    ) {

        val binArgs = createCliArgs(input, true, dirName, reportPath, moduleName)
        val binProc = ProcessBuilder().command(*binArgs).start()
        translateBinaryBundle(binProc.inputStream.readAllBytes(), dirName, CommonJsNameResolver(), reportPath)
    }
}


fun createStandardCliTranslator(): CliTranslator {
    return CliTranslator(
            "../node-package/build/env.json",
            "../node-package/build/distrib/bin/dukat-cli.js"
    )
}