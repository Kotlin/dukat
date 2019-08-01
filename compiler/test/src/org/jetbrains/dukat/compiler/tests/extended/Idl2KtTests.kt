package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.compiler.tests.FileFetcher
import org.jetbrains.dukat.compiler.tests.OutputTests
import org.jetbrains.dukat.compiler.translator.IdlInputTranslator
import org.jetbrains.dukat.idlReferenceResolver.DirectoryReferencesResolver
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.translator.ModuleTranslationUnit
import org.jetbrains.dukat.translator.TranslationUnitResult
import org.jetbrains.dukat.translatorString.WEBIDL_DECLARATION_EXTENSION
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class Idl2KtTests : OutputTests() {

    @DisplayName("idl2kt test set")
    @ParameterizedTest(name = "{0}")
    @MethodSource("idl2ktSet")
    @EnabledIfSystemProperty(named = "dukat.test.idl2kt", matches = "true")
    fun withValueSource(name: String, tsPath: String, ktPath: String) {
        assertContentEquals(name, tsPath, ktPath)
    }

    override fun concatenate(fileName: String, translated: List<TranslationUnitResult>): String {

        val (successfullTranslations, failedTranslations) = translated.partition { it is ModuleTranslationUnit }

        if (failedTranslations.isNotEmpty()) {
            throw Exception("translation failed")
        }
        val units = successfullTranslations.filterIsInstance(ModuleTranslationUnit::class.java)
        return units.find { File(it.fileName).canonicalFile == File(fileName).canonicalFile }?.
                content ?: "//NO DECLARATIONS"
    }

    override fun getTranslator(): InputTranslator = translator

    companion object : FileFetcher() {
        override val postfix = WEBIDL_DECLARATION_EXTENSION

        @JvmStatic
        fun idl2ktSet(): Array<Array<String>> {
            return fileSetWithDescriptors("./test/data/idl2kt")
        }

        val translator: InputTranslator = IdlInputTranslator(DirectoryReferencesResolver())
    }

}