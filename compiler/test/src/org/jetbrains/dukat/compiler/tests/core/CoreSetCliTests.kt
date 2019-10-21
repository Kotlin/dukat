package org.jetbrains.dukat.compiler.tests.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import org.jetbrains.dukat.compiler.tests.CliTranslator
import org.jetbrains.dukat.compiler.tests.FileFetcher
import org.jetbrains.dukat.compiler.tests.OutputTests
import org.jetbrains.dukat.compiler.tests.createStandardCliTranslator
import org.jetbrains.dukat.panic.resolvePanicMode
import org.jetbrains.dukat.translatorString.TS_DECLARATION_EXTENSION
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import kotlin.test.assertEquals


@Serializable
private data class ReportJson(val outputs: List<String>)

class CoreSetCliTests {

    init { resolvePanicMode() }

    @DisplayName("core test set [cli run]")
    @ParameterizedTest(name = "{0}")
    @MethodSource("coreSet")
    @EnabledIfSystemProperty(named = "dukat.test.cli", matches = "true")
    fun withValueSource(name: String, tsPath: String, ktPath: String) {
        assertContentEqualsBinary(name, tsPath, ktPath)
    }

    fun getTranslator(): CliTranslator = createStandardCliTranslator()

    companion object : FileFetcher() {

        override val postfix = TS_DECLARATION_EXTENSION

        @JvmStatic
        fun coreSet(): Array<Array<String>> {
            return fileSetWithDescriptors("./test/data/typescript")
        }
    }


    @UseExperimental(UnstableDefault::class)
    protected fun assertContentEqualsBinary(
            descriptor: String,
            tsPath: String,
            ktPath: String
    ) {

        val reportPath = "./build/reports/core/cli/${descriptor}.json"
        val dirName = "./build/tests/core/cli/${descriptor}"
        val translationResult = getTranslator().translate(tsPath, dirName, reportPath, "<RESOLVED_MODULE_NAME>")

        assertEquals(0, translationResult, "translation failed")

        val reportJson = Json.nonstrict.parse(ReportJson.serializer(), File(reportPath).readText())

        var translated = reportJson.outputs.mapNotNull { output ->
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
            } else {
                targetFile.readText()
            }
        }.joinToString(OutputTests.SEPARATOR)

        if (translated.isEmpty()) {
            translated = "// NO DECLARATIONS"
        }

        assertEquals(
                translated,
                File(ktPath).readText().trimEnd()
        )
    }

}

