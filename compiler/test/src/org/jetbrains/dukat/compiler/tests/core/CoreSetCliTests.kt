package org.jetbrains.dukat.compiler.tests.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import org.jetbrains.dukat.compiler.tests.CliTranslator
import org.jetbrains.dukat.compiler.tests.FileFetcher
import org.jetbrains.dukat.compiler.tests.OutputTests
import org.jetbrains.dukat.compiler.tests.createStandardCliTranslator
import org.jetbrains.dukat.compiler.tests.extended.CliTestsEnded
import org.jetbrains.dukat.compiler.tests.extended.CliTestsStarted
import org.jetbrains.dukat.compiler.tests.toFileUriScheme
import org.jetbrains.dukat.panic.resolvePanicMode
import org.jetbrains.dukat.translatorString.D_TS_DECLARATION_EXTENSION
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import kotlin.test.assertEquals


@Serializable
data class ReportJson(val outputs: List<String>)

class ResolvePanicMode : BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext?) {
        resolvePanicMode()
    }
}

@ExtendWith(ResolvePanicMode::class, CliTestsStarted::class, CliTestsEnded::class)
open class CoreSetCliTests {
    @DisplayName("core test set [cli run]")
    @ParameterizedTest(name = "{0}")
    @MethodSource("coreSet")
    open fun withValueSource(name: String, tsPath: String, ktPath: String) {
        assertContentEqualsBinary(name, tsPath, ktPath)
    }

    open fun getTranslator(): CliTranslator = createStandardCliTranslator()

    companion object : FileFetcher() {
        override val postfix = D_TS_DECLARATION_EXTENSION

        @JvmStatic
        fun coreSet(): Array<Array<String>> {
            return fileSetWithDescriptors("./test/data/typescript")
        }
    }

    @OptIn(UnstableDefault::class)
    protected fun assertContentEqualsBinary(
            descriptor: String,
            tsPath: String,
            ktPath: String
    ) {
        println("\nSOURCE:\t${tsPath.toFileUriScheme()}\nTARGET:\t${ktPath.toFileUriScheme()}")

        val reportPath = "./build/reports/core/cli/${descriptor}.json"
        val dirName = "./build/tests/core/cli/${descriptor}"
        getTranslator().translate(tsPath, dirName, reportPath, "<RESOLVED_MODULE_NAME>")

        @Suppress("DEPRECATION") val reportJson = Json.nonstrict.parse(ReportJson.serializer(), File(reportPath).readText())

        var translatedOutput = reportJson.outputs.mapNotNull { output ->
            println("OUTPUT ${output}")
            val targetFile = File(dirName, output)

            //TODO: unify with OutputTests.SKIPPED_DECLARATIONS
            val skippedDeclarations = setOf(
                    "Q.Q.kt",
                    "Q.kt",
                    "_skippedReferenced.kt",
                    "jquery.kt",
                    "node-ffi-buffer.kt",
                    "ref-array.kt",
                    "ref-array.ref_array.kt",
                    "ref.kt",
                    "ref.ref.kt"
            )
            if (skippedDeclarations.contains(targetFile.name)) {
                null
            } else if (output.startsWith("lib.")) {
                null
            } else {
                targetFile.readText()
            }
        }

        println("CLI TESTS ${reportJson.outputs.size} => ${translatedOutput.size}")
        val translated = translatedOutput.joinToString(OutputTests.SEPARATOR)

        assertEquals(
                if (translated.isEmpty()) {
                    "// NO DECLARATIONS"
                } else {
                    translated
                },
                File(ktPath).readText().trimEnd()
        )
    }

}

