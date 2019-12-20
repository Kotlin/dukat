package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.compiler.tests.CliTranslator
import org.jetbrains.dukat.compiler.tests.CompileMessageCollector
import org.jetbrains.dukat.compiler.tests.createStandardCliTranslator
import org.jetbrains.dukat.compiler.tests.httpService.CliHttpClient
import org.jetbrains.dukat.compiler.tests.httpService.CliHttpService
import org.jetbrains.dukat.compiler.tests.toFileUriScheme
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.arguments.K2JSCompilerArguments
import org.jetbrains.kotlin.cli.js.K2JSCompiler
import org.jetbrains.kotlin.config.Services
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import java.io.File
import kotlin.test.assertEquals

private var CLI_PROCESS: Process? = null
private val PORT = "8090"

class CliTestsStarted : BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext?) {
        CLI_PROCESS = CliHttpService().startService(PORT)
        CliHttpClient(PORT).waitForServer()
        println("cli http process creation: ${CLI_PROCESS?.isAlive}")
    }
}

class CliTestsEnded : AfterAllCallback {
    override fun afterAll(context: ExtensionContext?) {
        CLI_PROCESS?.destroy()
        println("shutting down cli http process")
    }
}


private class TestsEnded : AfterAllCallback {
    override fun afterAll(context: ExtensionContext?) {
        CompilationTests.report()
    }
}

@ExtendWith(CliTestsStarted::class, CliTestsEnded::class, TestsEnded::class)
abstract class CompilationTests {

    private fun getTranslator(): CliTranslator = createStandardCliTranslator()

    companion object {
        val COMPILATION_ERROR_ASSERTION = "COMPILATION ERROR"
        val FILE_NOT_FIND_ASSERTION = "FILE NOT FOUND"
        val START_TIMESTAMP = System.currentTimeMillis()

        private val reportData: MutableMap<String, Int> = mutableMapOf()

        fun report() {
            var total = 0
            println("ERRORS")
            reportData.toList().sortedByDescending { it.second }.forEach { (key, value) ->
                println("${key}: ${value} ")
                total += value
            }
            println("TOTAL: ${total}")
        }
    }

    abstract fun runTests(
            descriptor: String,
            sourcePath: String
    )

    protected fun compile(descriptor: String, sources: List<String>, targetPath: String): ExitCode {

        val options =
                K2JSCompilerArguments().apply {
                    outputFile = targetPath
                    metaInfo = false
                    sourceMap = false
                    noStdlib = true
                    moduleKind = "commonjs"
                    libraries = listOf(
                            "./build/kotlinHome/kotlin-stdlib-js.jar"
                    ).joinToString(File.pathSeparator)
                }

        options.freeArgs = sources

        reportData[descriptor] = 0
        val messageCollector = CompileMessageCollector { _, _, _ ->
            reportData[descriptor] = reportData.getOrDefault(descriptor, 0) + 1
        }

        return K2JSCompiler().exec(
                messageCollector,
                Services.EMPTY,
                options
        )
    }

    protected fun assertContentCompiles(
            descriptor: String, sourcePath: String
    ) {
        println(sourcePath.toFileUriScheme())
        val targetPath = "./build/tests/compiled/$START_TIMESTAMP/$descriptor"
        val targetDir = File(targetPath)
        println(targetDir.normalize().absolutePath.toFileUriScheme())

        targetDir.deleteRecursively()
        getTranslator().translate(sourcePath, targetPath)

        val outSource = "${targetPath}/$START_TIMESTAMP/${descriptor}.js"

        val sources = targetDir.walk().map { it.normalize().absolutePath }.toList()

        assert(sources.isNotEmpty()) { "$FILE_NOT_FIND_ASSERTION: $targetPath" }

        val compilationErrorMessage = "$COMPILATION_ERROR_ASSERTION:\n" + sources.joinToString("\n") { source -> source.toFileUriScheme() }

        assertEquals(
                ExitCode.OK,
                compile(
                        descriptor,
                        sources,
                        outSource
                ), compilationErrorMessage
        )
    }

}