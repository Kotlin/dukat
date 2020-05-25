package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.compiler.tests.FileFetcher
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class TypescriptDukatCompilationTest : CompilationTests() {
    @DisplayName("typescript dukat test")
    @ParameterizedTest(name = "{0}")
    @MethodSource("typescriptDukatSet")
    @EnabledIfSystemProperty(named = "dukat.test.typescriptDukat", matches = "true")
    override fun runTests(
        descriptor: String,
        sourcePath: String
    ) {
        assertContentCompiles(descriptor, sourcePath)
    }

    companion object  {
        private const val inputPath = "../typescript/ts-converter/src/"
        private const val entryPoint = "converter.ts"

        @JvmStatic
        fun typescriptDukatSet(): Array<Array<String>> {
            return arrayOf(arrayOf(
                "converter",
                File(inputPath + entryPoint).normalize().absolutePath
            ))
        }
    }
}