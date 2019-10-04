package org.jetbrains.dukat.compiler.tests.core

import org.jetbrains.dukat.compiler.tests.FileFetcher
import org.jetbrains.dukat.compiler.tests.OutputTests
import org.jetbrains.dukat.compiler.translator.JSModuleTranslator
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.translatorString.JS_DECLARATION_EXTENSION
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class JSTests : OutputTests() {

    @DisplayName("js test set")
    @ParameterizedTest(name = "{0}")
    @MethodSource("jsSet")
    fun withValueSource(name: String, jsPath: String, ktPath: String) {
        assertContentEquals(name, jsPath, ktPath)
    }

    override fun getTranslator(): InputTranslator<String> = translator

    companion object : FileFetcher() {
        override val postfix = JS_DECLARATION_EXTENSION

        @JvmStatic
        fun jsSet(): Array<Array<String>> {
            return fileSetWithDescriptors("./test/data/javascript")
        }

        val translator: InputTranslator<String> = JSModuleTranslator()
    }
}