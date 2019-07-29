package org.jetbrains.dukat.compiler.tests.core

import org.jetbrains.dukat.compiler.tests.FileFetcher
import org.jetbrains.dukat.compiler.tests.OutputTests
import org.jetbrains.dukat.compiler.translator.IdlInputTranslator
import org.jetbrains.dukat.idlReferenceResolver.EmptyReferencesResolver
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.translatorString.WEBIDL_DECLARATION_EXTENSION
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class IdlTests : OutputTests() {

    @DisplayName("idl test set")
    @ParameterizedTest(name = "{0}")
    @MethodSource("idlSet")
    fun withValueSource(name: String, tsPath: String, ktPath: String) {
        assertContentEquals(name, tsPath, ktPath)
    }

    override fun getTranslator(): InputTranslator = translator

    companion object : FileFetcher() {
        override val postfix = WEBIDL_DECLARATION_EXTENSION

        @JvmStatic
        fun idlSet(): Array<Array<String>> {
            return fileSetWithDescriptors("./test/data/idl")
        }

        val translator: InputTranslator = IdlInputTranslator(EmptyReferencesResolver())
    }

}