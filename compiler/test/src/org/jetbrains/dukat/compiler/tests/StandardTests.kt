package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.compiler.createNashornTranslator
import org.jetbrains.dukat.compiler.createV8Translator
import org.jetbrains.dukat.compiler.output
import org.jetbrains.dukat.compiler.translator.InputTranslator
import java.io.File
import kotlin.test.assertEquals

open class StandardTests {
    companion object {
        val translator: InputTranslator

        init {
            if (System.getenv("DUKAT_RUNTIME") == "NASHORN") {
                println("nashorn runtime")
                translator = createNashornTranslator()
            } else {
                println("v8 runtime")
                translator = createV8Translator()
            }
        }

    }

    protected fun assertContentEquals(descriptor: String, tsPath: String, ktPath: String, output: (String, InputTranslator) -> String? = ::output) {

        val targetShortName = "${descriptor}.d.kt"

        val translated = output(tsPath, translator)
        val outputDirectory = File("./build/tests/out")
        translated?.let {
            val outputFile = outputDirectory.resolve(targetShortName)
            outputFile.parentFile.mkdirs()
            outputFile.writeText(translated)
        }

        assertEquals(
                translated,
                File(ktPath).readText().trimEnd()
        )
    }

}