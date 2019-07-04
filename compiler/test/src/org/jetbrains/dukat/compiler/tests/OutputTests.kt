package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.panic.PanicMode
import org.jetbrains.dukat.panic.setPanicMode
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.translator.ModuleTranslationUnit
import org.jetbrains.dukat.translator.TranslationUnitResult
import translateModule
import java.io.File
import kotlin.test.assertEquals


abstract class OutputTests {
    abstract fun getTranslator(): InputTranslator

    init {
        if (System.getProperty("dukat.test.failure.always") == "true") {
            setPanicMode(PanicMode.ALWAYS_FAIL)
        }
    }

    private fun concatenate(translated: List<TranslationUnitResult>): String {

        val (successfullTranslations, failedTranslations) = translated.partition { it is ModuleTranslationUnit }

        if (failedTranslations.isNotEmpty()) {
            throw Exception("translation failed")
        }

        val units = successfullTranslations.filterIsInstance(ModuleTranslationUnit::class.java)
        return if (units.isEmpty()) {
            "// NO DECLARATIONS"
        } else {
            val skipDeclarations = setOf(
                    "jquery.d.ts",
                    "node-ffi-buffer.d.ts",
                    "ref-array.d.ts",
                    "ref.d.ts",
                    "Q.d.ts",
                    "_skippedReferenced.d.ts"
            )
            units.filter { (name, fileName, _, _) ->
                !skipDeclarations.contains(File(fileName).name)
            }.joinToString("""

// ------------------------------------------------------------------------------------------
""".replace("\n", System.getProperty("line.separator"))) { it.content }
        }
    }

    private fun output(fileName: String, translator: InputTranslator): String {
        return concatenate(translateModule(fileName, translator))
    }


    protected fun assertContentEquals(
            descriptor: String,
            tsPath: String,
            ktPath: String
    ) {

        val targetShortName = "${descriptor}.d.kt"

        val translated = output(tsPath, getTranslator())

        assertEquals(
                translated,
                File(ktPath).readText().trimEnd()
        )

        val outputDirectory = File("./build/tests/out")
        translated?.let {
            val outputFile = outputDirectory.resolve(targetShortName)
            outputFile.parentFile.mkdirs()
            outputFile.writeText(translated)
        }
    }

}