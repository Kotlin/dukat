package org.jetbrains.dukat.compiler.tests.core

import org.jetbrains.dukat.compiler.tests.MethodSourceSourceFiles
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

    override fun getTranslator(): InputTranslator<String> = translator

    companion object {
        @JvmStatic
        fun idlSet(): Array<Array<String>> {
            return MethodSourceSourceFiles("./test/data/idl", WEBIDL_DECLARATION_EXTENSION).fileSetWithDescriptors()
        }

        val translator: InputTranslator<String> = IdlInputTranslator(EmptyReferencesResolver())
    }

}