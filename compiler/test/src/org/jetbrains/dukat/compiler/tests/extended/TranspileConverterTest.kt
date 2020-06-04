package org.jetbrains.dukat.compiler.tests.extended

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class TranspileConverterTest : CompilationTests() {
    @DisplayName("typescript dukat test")
    @ParameterizedTest(name = "{0}")
    @MethodSource("typescriptDukatSet")
    @EnabledIfSystemProperty(named = "dukat.test.typescriptDukat", matches = "true")
    override fun runTests(
        descriptor: String,
        sourcePath: String,
        tsConfig: String
    ) {
        assertContentCompiles(descriptor, sourcePath, if (tsConfig.isEmpty()) null else tsConfig)
    }

    companion object  {
        private const val inputPath = "../typescript/ts-converter/src/"
        private const val entryPoint = "converter.ts"

        @JvmStatic
        fun typescriptDukatSet(): Array<Array<String>> {
            return arrayOf(arrayOf(
                "converter",
                File(inputPath + entryPoint).normalize().absolutePath,
                File(inputPath, "../tsconfig.json").absolutePath
            ))
        }
    }
}