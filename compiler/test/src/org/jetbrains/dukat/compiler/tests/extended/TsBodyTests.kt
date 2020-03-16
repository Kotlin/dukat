package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.compiler.tests.CliBodyTranslator
import org.jetbrains.dukat.compiler.tests.CliTranslator
import org.jetbrains.dukat.compiler.tests.FileFetcher
import org.jetbrains.dukat.compiler.tests.core.CoreSetCliTests
import org.jetbrains.dukat.translatorString.TS_DECLARATION_EXTENSION
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class TsBodyTests : CoreSetCliTests() {

    @DisplayName("ts body test set")
    @ParameterizedTest(name = "{0}")
    @MethodSource("typescriptBodiesSet")
    @EnabledIfSystemProperty(named = "dukat.test.tsBody", matches = "true")
    override fun withValueSource(name: String, tsPath: String, ktPath: String) {
        assertContentEqualsBinary(name, tsPath, ktPath)
    }

    override fun getTranslator(): CliTranslator = CliBodyTranslator()

    companion object : FileFetcher() {
        override val postfix = TS_DECLARATION_EXTENSION

        @JvmStatic
        fun typescriptBodiesSet(): Array<Array<String>> {
            return fileSetWithDescriptors("./test/data/typescriptBodies", resultPostfix = ".kt")
        }
    }
}
