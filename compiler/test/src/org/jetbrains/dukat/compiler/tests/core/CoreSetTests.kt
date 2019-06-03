package org.jetbrains.dukat.compiler.tests.core

import org.jetbrains.dukat.compiler.createGraalTranslator
import org.jetbrains.dukat.compiler.tests.OutputTests
import org.jetbrains.dukat.translator.InputTranslator
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class CoreSetTests : OutputTests() {

    @DisplayName("core test set")
    @ParameterizedTest(name = "{0}")
    @MethodSource("coreSet")
    fun withValueSource(name: String, tsPath: String, ktPath: String) {
        assertContentEquals(name, tsPath, ktPath)
    }

    override fun getTranslator(): InputTranslator = translator

    companion object {
        @JvmStatic
        fun coreSet(): Array<Array<String>> {
            return fileSetWithDescriptors("./test/data")
        }

        val translator: InputTranslator = createGraalTranslator()
    }
}