package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.panic.PanicMode
import org.jetbrains.dukat.panic.setPanicMode
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.translatorString.ModuleTranslationUnit
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.arguments.K2JSCompilerArguments
import org.jetbrains.kotlin.cli.js.K2JSCompiler
import org.jetbrains.kotlin.config.Services
import translateModule
import java.io.File
import kotlin.test.assertEquals


abstract class OutputTests {
    abstract fun getTranslator(): InputTranslator

    init {
        if (System.getProperty("dukat.test.failure.always") == "true") {
            setPanicMode(PanicMode.ALWAYS_FAIL)
        }
    }

    private fun compile(sourcePath: String, targetPath: String): ExitCode {
        val options =
                K2JSCompilerArguments().apply {
                    outputFile = targetPath
                    metaInfo = false
                    sourceMap = false
                    kotlinHome = "./build"
                }

        options.freeArgs = listOf(sourcePath)
        return K2JSCompiler().exec(
                CompileMessageCollector(),
                Services.EMPTY,
                options
        )
    }

    private fun concatenate(translated: List<ModuleTranslationUnit>): String {

        return if (translated.isEmpty()) {
            "// NO DECLARATIONS"
        } else {
            val skipDeclarations = setOf(
                    "jquery.d.ts",
                    "node-ffi-buffer.d.ts",
                    "ref-array.d.ts",
                    "ref.d.ts",
                    "Q.d.ts",
                    "_skippedReferenced.d.ts"
            )
            translated.filter { (fileName, _, _) ->
                !skipDeclarations.contains(File(fileName).name)
            }.joinToString("""

// ------------------------------------------------------------------------------------------
""".replace("\n", System.getProperty("line.separator"))) { it.content }
        }
    }

    private fun output(fileName: String, translator: InputTranslator): String {
        return concatenate(translateModule(fileName, translator))
    }


    protected fun assertContentEquals(
            descriptor: String,
            tsPath: String,
            ktPath: String
    ) {

        val targetShortName = "${descriptor}.d.kt"

        val translated = output(tsPath, getTranslator())

        assertEquals(
                translated,
                File(ktPath).readText().trimEnd()
        )

        val outputDirectory = File("./build/tests/out")
        translated?.let {
            val outputFile = outputDirectory.resolve(targetShortName)
            outputFile.parentFile.mkdirs()
            outputFile.writeText(translated)
        }
    }

    protected fun assertContentCompiles(
            descriptor: String,
            tsPath: String,
            output: (String, InputTranslator) -> String? = ::output
    ) {

        val targetShortName = "${descriptor}.d.kt"

        val translated = output(tsPath, getTranslator())

        val outputDirectory = File("./build/tests/out")
        translated?.let {
            val outputFile = outputDirectory.resolve(targetShortName)
            outputFile.parentFile.mkdirs()
            outputFile.writeText(translated)

            val sourcePath = outputFile.absolutePath
            assertEquals(ExitCode.OK, compile(sourcePath, sourcePath.replace(".kt", ".js")), translated)
        }
    }


    companion object {
        val TS_POSTFIX = ".d.ts"

        fun fileSet(path: String, postfix: String, recursive: Boolean = false): Sequence<File> {
            val rootFolder = File(path)

            val files = if (recursive) rootFolder.walk() else rootFolder.walkTopDown()

            return files.filter { file ->
                file.path.endsWith(postfix)
            }
        }

        fun fileSetWithDescriptors(path: String, recursive: Boolean = false): Array<Array<String>> {
            val rootFolder = File(path)
            return fileSet(path, TS_POSTFIX, recursive).mapNotNull { file ->
                val tsPath = file.absolutePath
                val ktPath = tsPath.replace(TS_POSTFIX, ".d.kt")
                val descriptor = file.relativeTo(rootFolder).path.replace(path, "").replace(TS_POSTFIX, "")

                if (!file.name.startsWith("_")) {
                    arrayOf(
                            descriptor,
                            tsPath,
                            ktPath
                    )
                } else null
            }.toList().toTypedArray()
        }

    }


}