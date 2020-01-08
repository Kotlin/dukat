package org.jetbrains.dukat.compiler.tests.core

import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import org.jetbrains.dukat.compiler.tests.CliTranslator
import org.jetbrains.dukat.compiler.tests.FileFetcher
import org.jetbrains.dukat.compiler.tests.OutputTests
import org.jetbrains.dukat.compiler.tests.createStandardCliTranslator
import org.jetbrains.dukat.compiler.tests.extended.CliTestsEnded
import org.jetbrains.dukat.compiler.tests.extended.CliTestsStarted
import org.jetbrains.dukat.compiler.tests.toFileUriScheme
import org.jetbrains.dukat.panic.PanicMode
import org.jetbrains.dukat.panic.setPanicMode
import org.jetbrains.dukat.translatorString.JS_DECLARATION_EXTENSION
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import kotlin.test.assertEquals

@ExtendWith(CliTestsStarted::class, CliTestsEnded::class)
class JSTypeCliTests {
    @DisplayName("js type test set [cli run]")
    @ParameterizedTest(name = "{0}")
    @MethodSource("jsSet")
    fun withValueSource(name: String, tsPath: String, ktPath: String) {
        assertContentEqualsBinary(name, tsPath, ktPath)
    }

    private fun getTranslator(): CliTranslator = createStandardCliTranslator()

    companion object : FileFetcher() {
        override val postfix = JS_DECLARATION_EXTENSION
        override val ktPostfix = ".kt"

        @JvmStatic
        fun jsSet(): Array<Array<String>> {
            return fileSetWithDescriptors("./test/data/javascript")
        }

        @JvmStatic
        @BeforeAll
        fun setup() {
            setPanicMode(PanicMode.NEVER_FAIL)
        }
    }

    @UseExperimental(UnstableDefault::class)
    protected fun assertContentEqualsBinary(
            descriptor: String,
            jsPath: String,
            ktPath: String
    ) {
        val reportPath = "./build/reports/js/cli/${descriptor}.json"
        val dirName = "./build/tests/js/cli/${descriptor}"
        getTranslator().translateJS(jsPath, dirName, reportPath, "<RESOLVED_MODULE_NAME>")

        val reportJson = Json.nonstrict.parse(ReportJson.serializer(), File(reportPath).readText())

        var translated = reportJson.outputs.joinToString(OutputTests.SEPARATOR) { output ->
            println("OUTPUT ${output}")
            File(dirName, output).readText()
        }

        if (translated.isEmpty()) {
            translated = "// NO DECLARATIONS"
        }

        assertEquals(
                translated,
                File(ktPath).readText().trimEnd(),
                "\nSOURCE:\t${jsPath.toFileUriScheme()}\nTARGET:\t${ktPath.toFileUriScheme()}"
        )
    }

}

