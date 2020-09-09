package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.compiler.tests.CliTranslator
import org.jetbrains.dukat.compiler.tests.CompileMessageCollector
import org.jetbrains.dukat.compiler.tests.toFileUriScheme
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.arguments.K2JSCompilerArguments
import org.jetbrains.kotlin.cli.js.K2JSCompiler
import org.jetbrains.kotlin.config.Services
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import java.io.File
import java.io.PrintStream
import kotlin.test.assertEquals

private class TestsEnded : AfterAllCallback {
    override fun afterAll(context: ExtensionContext?) {
        val buildNumber = System.getenv("BUILD_NUMBER") ?: ""
        val projectName = System.getenv("TEAMCITY_BUILDCONF_NAME")?.replace(" ", "_") ?: ""
        CompilationTests.report("build/reports/compilation_${context?.displayName}_${projectName}_${buildNumber}.txt")
    }
}

private data class ReportData(var errorCount: Int, var translationTime: Long, var compilationTime: Long, var compilationResult: ExitCode, var errorMessage: String?)
private fun MutableMap<String, ReportData>.getReportFor(reportName: String): ReportData {
    return getOrPut(reportName) { ReportData(0, 0, 0, ExitCode.OK, null) }
}

private class TiedPrintStream(private val mainStream: PrintStream, private val secondStream: PrintStream) : PrintStream(mainStream) {
    override fun println(x: String?) {
        super.println(x)
        secondStream.println(x)
    }
}

@ExtendWith(CliTestsStarted::class, CliTestsEnded::class, TestsEnded::class)
abstract class CompilationTests {

    protected fun getTranslator(): CliTranslator = CliTranslator()

    companion object {
        val COMPILATION_ERROR_ASSERTION = "COMPILATION ERROR"
        val FILE_NOT_FIND_ASSERTION = "FILE NOT FOUND"
        val START_TIMESTAMP = System.currentTimeMillis()

        private val reportDataMap: MutableMap<String, ReportData> = mutableMapOf()

        fun report(fileName: String?) {
            val printStream = if (fileName == null) { System.out } else { TiedPrintStream(PrintStream(fileName), System.out) }
            val passed = reportDataMap.values.count { it.compilationResult == ExitCode.OK }
            val total = reportDataMap.values.size
            printStream.println("COMPILATION REPORT ${passed}/${total}")
            val namePadding = reportDataMap.keys.maxByOrNull { it.length }?.length ?: 24
            printStream.println(java.lang.String.format("%-${namePadding}s\t%-17s\t%-6s\t%-7s\t%-5s", "name", "result", "trans.", "comp.", "error"))
            val formatString = "%-${namePadding}s\t%-17s\t%6s\t%7s\t%5d"
            reportDataMap.toList().sortedByDescending { it.second.errorCount }.forEach { (key, reportData) ->
                val errorCount = reportData.errorCount
                @Suppress("UNUSED_VARIABLE") val errorMessage = reportData.errorMessage?.let { it.substringBefore("\n") } ?: ""
                printStream.println(java.lang.String.format(formatString, key, reportData.compilationResult, "${reportData.translationTime}ms", "${reportData.compilationTime}ms", errorCount))
            }
            printStream.println("")
            printStream.println("ERRORS: ${reportDataMap.values.map { it.errorCount }.sum()}")
            val translationTimes     = reportDataMap.values.map { it.translationTime }
            printStream.println("AVG TRANSLATION TIME: ${translationTimes.average()}ms")
            val compilationTimes = reportDataMap.values.map { it.compilationTime }
            printStream.println("AVG COMPILATION TIME: ${compilationTimes.average()}ms")
        }
    }

    abstract fun runTests(
            descriptor: String,
            sourcePath: String,
            tsConfig: String
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

        val messageCollector = CompileMessageCollector { errorMessage, _, _ ->
            val report = reportDataMap.getReportFor(descriptor)
            report.errorCount += 1
            if (report.errorMessage == null) {
                report.errorMessage = errorMessage
            }
        }

        val compilationStarted = System.currentTimeMillis()
        return K2JSCompiler().exec(
                messageCollector,
                Services.EMPTY,
                options
        ).also {
            val reportForDescritpor = reportDataMap.getReportFor(descriptor)
            reportForDescritpor.compilationResult = it
            reportForDescritpor.compilationTime = System.currentTimeMillis() - compilationStarted
        }
    }

    protected fun assertContentCompiles(
            descriptor: String, sourcePath: String, tsConfig: String? = null
    ) {
        println(sourcePath.toFileUriScheme())
        val targetPath = "./build/tests/compiled/$START_TIMESTAMP/$descriptor"
        val targetDir = File(targetPath)
        println(targetDir.normalize().absolutePath.toFileUriScheme())

        targetDir.deleteRecursively()

        val translationStarted = System.currentTimeMillis()
        getTranslator().convert(sourcePath, tsConfig, targetPath, false, null)
        reportDataMap.getReportFor(descriptor).translationTime = System.currentTimeMillis() - translationStarted

        val outSource = "${targetPath}/$START_TIMESTAMP/${descriptor}.js"

        val sources = targetDir.walk().map { it.normalize().absolutePath }.toList()

        assert(sources.isNotEmpty()) { "$FILE_NOT_FIND_ASSERTION: $targetPath" }

        val compilationErrorMessage = "$COMPILATION_ERROR_ASSERTION:\n" + sources.joinToString("\n") { source -> source.toFileUriScheme() }

        val compilationResult = compile(
                descriptor,
                sources,
                outSource
        )

        assertEquals(
                ExitCode.OK,
                compilationResult,
                compilationErrorMessage
        )
    }

}