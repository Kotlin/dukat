package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.compiler.tests.core.TestConfig.DEFINITELY_TYPED_DIR
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.opentest4j.AssertionFailedError
import java.io.File


private fun File.isDefinitelyTypedDeclaration(): Boolean {
    return isFile() && name == "index.d.ts"
}

private val startTimes: MutableMap<String, Long> = mutableMapOf()
private val totalTimes: MutableMap<String, TestData> = mutableMapOf()

private enum class TestStatus {
    OK,
    FAILED_COMPILATION,
    FAILED_TRANSLATION,
    UNKNOWN
}

private data class TestData(val descriptor: String, val executionTime: Long, val status: TestStatus)


private class TestStarted : BeforeTestExecutionCallback {
    override fun beforeTestExecution(context: ExtensionContext?) {
        startTimes[context!!.displayName] = System.currentTimeMillis()
    }
}

private class TestEnded : AfterTestExecutionCallback {
    override fun afterTestExecution(context: ExtensionContext?) {
        val displayName = context!!.displayName
        val status = when {
            context.executionException.isPresent -> {
                val exception = context.executionException.get()
                when (exception) {
                    is AssertionFailedError -> {
                        val message = exception.message!!
                        when {
                            message.startsWith(CompilationTests.FILE_NOT_FIND_ASSERTION) -> TestStatus.FAILED_TRANSLATION
                            message.startsWith(CompilationTests.COMPILATION_ERROR_ASSERTION) -> TestStatus.FAILED_COMPILATION
                            else -> TestStatus.UNKNOWN
                        }
                    }
                    else -> TestStatus.UNKNOWN
                }
            }
            else -> TestStatus.OK
        }

        println(context.executionException)

        totalTimes[displayName] = TestData(
                displayName,
                System.currentTimeMillis() - startTimes[displayName]!!,
                status
        )
    }
}

private class TestSuiteEnded : AfterAllCallback {
    override fun afterAll(context: ExtensionContext?) {

        val report = File("./build/reports/report.csv");
        report.writeText("")

        val times = totalTimes.values.sortedByDescending { it.executionTime }

        report.appendText("name, time(millis), status\n")
        times.forEach { (key, value, status) ->
            report.appendText("$key, $status, $value\n")
        }

        report.appendText("AVG, ${times.map { it.executionTime }.average()}\n")
    }
}

@ExtendWith(TestStarted::class, TestEnded::class, TestSuiteEnded::class)
class DefinitelyTypedCompilationTests : CompilationTests() {
    @DisplayName("core test set compile")
    @ParameterizedTest(name = "{0}")
    @MethodSource("extendedSet")
    @EnabledIfSystemProperty(named = "dukat.test.extended", matches = "true")
    override fun runTests(
            descriptor: String,
            sourcePath: String
    ) {
        assertContentCompiles(descriptor, sourcePath)
    }

    companion object {
        @JvmStatic
        fun extendedSet(): Array<Array<String>> {

            val filterFunc: (File) -> Boolean = if (System.getProperty("dukat.test.definitelyTyped.repexp") == null) {
                File::isDefinitelyTypedDeclaration
            } else {
                val nameRegex = Regex(System.getProperty("dukat.test.definitelyTyped.repexp"), RegexOption.IGNORE_CASE)
                ({ file: File -> file.isDefinitelyTypedDeclaration() && nameRegex.matches(file.parentFile.name) })
            }

            val files = File(DEFINITELY_TYPED_DIR).walk()
                    .filter(filterFunc)
                    .map {
                        arrayOf(
                                it.parentFile.name,
                                it.absolutePath
                        )
                    }.toList().toTypedArray()

            return files
        }

    }
}