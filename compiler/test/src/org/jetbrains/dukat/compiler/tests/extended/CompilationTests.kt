package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.compiler.tests.CliTranslator
import org.jetbrains.dukat.compiler.tests.CompileMessageCollector
import org.jetbrains.dukat.compiler.tests.createStandardCliTranslator
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.arguments.K2JSCompilerArguments
import org.jetbrains.kotlin.cli.js.K2JSCompiler
import org.jetbrains.kotlin.config.Services
import org.junit.jupiter.api.BeforeAll
import java.io.File
import kotlin.test.assertEquals

abstract class CompilationTests {

    private fun getTranslator(): CliTranslator = createStandardCliTranslator()

    abstract fun runTests(
            descriptor: String,
            sourcePath: String
    )

    protected fun compile(sources: List<String>, targetPath: String): ExitCode {

        val options =
                K2JSCompilerArguments().apply {
                    outputFile = targetPath
                    metaInfo = false
                    sourceMap = false
                    noStdlib = true
                    libraries = listOf(
                            "./build/kotlinHome/kotlin-stdlib-js.jar"
                    ).joinToString(File.pathSeparator)
                }

        options.freeArgs = sources

        return K2JSCompiler().exec(
                CompileMessageCollector(),
                Services.EMPTY,
                options
        )
    }

    companion object {
        val COMPILATION_ERROR_ASSERTION = "COMPILATION ERROR"
        val FILE_NOT_FIND_ASSERTION = "FILE NOT FOUND"
        val START_TIMESTAMP = System.currentTimeMillis()
    }

    protected fun assertContentCompiles(
            descriptor: String,
            sourcePath: String
    ) {

        val targetPath = "./build/tests/compiled/$START_TIMESTAMP/$descriptor"
        val targetDir = File(targetPath)

        targetDir.deleteRecursively()
        getTranslator().translate(sourcePath, targetPath)
        val outSource = "${targetPath}/$START_TIMESTAMP/${descriptor}.js"

        val sources = targetDir.walk().map { it.normalize().absolutePath }.toList()

        assert(sources.isNotEmpty()) { "$FILE_NOT_FIND_ASSERTION: $targetPath" }

        val compilationErrorMessage = "$COMPILATION_ERROR_ASSERTION:\n" + sources.joinToString("\n") { source -> "file:///${source}" }

        assertEquals(
                ExitCode.OK,
                compile(
                        sources,
                        outSource
                ), compilationErrorMessage
        )
    }

}