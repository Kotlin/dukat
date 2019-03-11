package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.compiler.createNashornTranslator
import org.jetbrains.dukat.compiler.createV8Translator
import org.jetbrains.dukat.compiler.output
import org.jetbrains.dukat.compiler.translator.InputTranslator
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.arguments.K2JSCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.js.K2JSCompiler
import org.jetbrains.kotlin.config.Services
import java.io.File
import kotlin.test.assertEquals


private class TestCompileMessageCollector : MessageCollector {
    private var myHasErrors: Boolean = false

    override fun clear() {
        myHasErrors = false
    }

    override fun hasErrors(): Boolean {
        return false
    }

    override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageLocation?) {
        if (severity.isError) {
            myHasErrors = true

            System.err.println("MESSAGE ${severity} ${message} ${location}")
        }
    }
}

open class StandardTests {
    companion object {
        val translator: InputTranslator

        init {
            if (System.getenv("DUKAT_RUNTIME") == "NASHORN") {
                println("nashorn runtime")
                translator = createNashornTranslator()
            } else {
                println("v8 runtime")
                translator = createV8Translator()
            }
        }

    }

    private fun compile(sourcePath: String, targetPath: String): ExitCode {
        println("TARGET ${targetPath}")

        val options =
                K2JSCompilerArguments().apply {
                    outputFile = targetPath
                    metaInfo = false
                    sourceMap = false
                    kotlinHome = "./build"
                }

        options.freeArgs = listOf(sourcePath)
        return K2JSCompiler().exec(
                TestCompileMessageCollector(),
                Services.EMPTY,
                options
        )
    }

    protected fun assertContentEquals(
            descriptor: String,
            tsPath: String,
            ktPath: String,
            output: (String, InputTranslator) -> String? = ::output
    ) {

        val targetShortName = "${descriptor}.d.kt"

        val translated = output(tsPath, translator)

        assertEquals(
                translated,
                File(ktPath).readText().trimEnd()
        )

        val outputDirectory = File("./build/tests/out")
        translated?.let {
            val outputFile = outputDirectory.resolve(targetShortName)
            outputFile.parentFile.mkdirs()
            outputFile.writeText(translated)

            val sourcePath = outputFile.absolutePath
//            assertEquals(ExitCode.OK, compile(sourcePath, sourcePath.replace(".kt", ".js")), translated)
        }
    }

    protected fun assertContentCompiles(
            descriptor: String,
            tsPath: String,
            output: (String, InputTranslator) -> String? = ::output
    ) {

        val targetShortName = "${descriptor}.d.kt"

        val translated = output(tsPath, translator)

        val outputDirectory = File("./build/tests/out")
        translated?.let {
            val outputFile = outputDirectory.resolve(targetShortName)
            outputFile.parentFile.mkdirs()
            outputFile.writeText(translated)

            val sourcePath = outputFile.absolutePath
            assertEquals(ExitCode.OK, compile(sourcePath, sourcePath.replace(".kt", ".js")), translated)
        }
    }


}