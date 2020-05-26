package org.jetbrains.dukat.compiler.tests.core

import org.jetbrains.dukat.compiler.tests.CliBodyTranslator
import org.jetbrains.dukat.compiler.tests.CliTranslator
import org.jetbrains.dukat.compiler.tests.MethodSourceSourceFiles
import org.jetbrains.dukat.translatorString.TS_DECLARATION_EXTENSION
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class TsBodyTests : CoreSetCliTests() {

    @DisplayName("ts body test set")
    @ParameterizedTest(name = "{0}")
    @MethodSource("typescriptBodiesSet")
    override fun withValueSource(name: String, tsPath: String, ktPath: String, tsConfig: String) {
        assertContentEqualsBinary(name, tsPath, ktPath, if (tsConfig.isEmpty()) null else tsConfig)
    }

    override fun getTranslator(): CliTranslator = CliBodyTranslator()

    companion object {
        @JvmStatic
        fun typescriptBodiesSet(): Array<Array<String>> {
            return MethodSourceSourceFiles("./test/data/typescriptBodies", TS_DECLARATION_EXTENSION, ".kt").fileSetWithDescriptors()
        }
    }
}
