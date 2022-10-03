package org.jetbrains.dukat.compiler.tests.core

import org.jetbrains.dukat.compiler.tests.OutputTests
import org.jetbrains.dukat.translator.ModuleTranslationUnit
import org.jetbrains.dukat.translator.TranslationUnitResult
import java.io.File

abstract class Idl2KtTestBase : OutputTests() {
    override fun concatenate(fileName: String, translated: List<TranslationUnitResult>): String {

        val (successfullTranslations, failedTranslations) = translated.partition { it is ModuleTranslationUnit }

        if (failedTranslations.isNotEmpty()) {
            throw Exception("translation failed")
        }
        val units = successfullTranslations.filterIsInstance(ModuleTranslationUnit::class.java)
        return units.find { File(it.fileName).canonicalFile == File(fileName).canonicalFile }?.
        content ?: "//NO DECLARATIONS"
    }
}