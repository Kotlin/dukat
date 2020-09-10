package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.panic.resolvePanicMode
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.translator.ModuleTranslationUnit
import org.jetbrains.dukat.translator.TranslationUnitResult
import org.jetbrains.dukat.translatorString.translateFile
import org.junit.jupiter.api.BeforeAll
import java.io.File
import kotlin.test.assertEquals


abstract class OutputTests {
    abstract fun getTranslator(): InputTranslator<String>

    companion object {

        @JvmStatic
        @BeforeAll
        fun setup() {
            resolvePanicMode()
        }

        val SEPARATOR: String = """

// ------------------------------------------------------------------------------------------
""".replace("\n", System.getProperty("line.separator"))
    }

    open fun concatenate(fileName: String, translated: List<TranslationUnitResult>): String {

        val (successfullTranslations, failedTranslations) = translated.partition { it is ModuleTranslationUnit }

        if (failedTranslations.isNotEmpty()) {
            throw Exception("translation failed")
        }

        val units = successfullTranslations.filterIsInstance(ModuleTranslationUnit::class.java)
        return if (units.isEmpty()) {
            "// NO DECLARATIONS"
        } else {
            units.joinToString(SEPARATOR) { it.content }
        }
    }

    private fun output(fileName: String, translator: InputTranslator<String>): String {
        return concatenate(fileName, translateFile(fileName, translator))
    }


    protected fun assertContentEquals(
            descriptor: String,
            tsPath: String,
            ktPath: String
    ) {

        @Suppress("UNUSED_VARIABLE") val targetShortName = "${descriptor}.d.kt"

        println("\nSOURCE:\t${tsPath.toFileUriScheme()}\nTARGET:\t${ktPath.toFileUriScheme()}")

        val translated = output(tsPath, getTranslator())

        assertEquals(
                translated,
                File(ktPath).readText().trimEnd()
        )
    }

}