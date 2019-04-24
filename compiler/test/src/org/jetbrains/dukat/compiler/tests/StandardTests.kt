package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.arguments.K2JSCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.js.K2JSCompiler
import org.jetbrains.kotlin.config.Services
import translateModule
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

fun compile(documentRoot: ModuleModel): String {
    val translated = translateModule(documentRoot)

    return if (translated.isEmpty()) {
        "// NO DECLARATIONS"
    } else {
        translated.joinToString("""

// ------------------------------------------------------------------------------------------
""".replace("\n", System.getProperty("line.separator")))
    }
}


private fun output(fileName: String, translator: InputTranslator): String {
    val sourceSet =
            translator.translate(fileName)

    val sourcesMap = mutableMapOf<String, SourceFileModel>()
    sourceSet.sources.map { sourceFileDeclaration ->
        sourcesMap[sourceFileDeclaration.fileName] = sourceFileDeclaration
    }


    val fileNameNormalized = File(fileName).normalize().absolutePath

    val documentRoot = sourcesMap.get(fileNameNormalized)?.root!!
    return compile(documentRoot)
}


abstract class StandardTests {
    abstract fun getTranslator(): InputTranslator

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


}