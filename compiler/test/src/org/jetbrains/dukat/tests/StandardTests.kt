package org.jetbrains.dukat.tests

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

    protected fun assertContentEquals(name: String, output: (String, InputTranslator) -> String? = ::output) {

        val resourceDirectory = File("./test/data")
        val fileNameSource = resourceDirectory.resolve("${name}.d.ts").absolutePath
        val fileNameTarget = resourceDirectory.resolve("${name}.d.kt")

        assertEquals(
                output(fileNameSource, translator),
                fileNameTarget.readText().trimEnd()
        )
    }

}