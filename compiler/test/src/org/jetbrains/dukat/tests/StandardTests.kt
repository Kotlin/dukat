package org.jetbrains.dukat.tests

import org.jetbrains.dukat.compiler.Translator
import org.jetbrains.dukat.compiler.compile
import org.jetbrains.dukat.compiler.createNashornTranslator
import org.jetbrains.dukat.compiler.createV8Translator
import java.io.File
import kotlin.test.assertEquals

open class StandardTests {
    companion object {
        val translator: Translator

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

    protected fun assertContentEquals(name: String) {

        val resourceDirectory = File("./test/data")
        val fileNameSource = resourceDirectory.resolve("${name}.d.ts").absolutePath
        val fileNameTarget = resourceDirectory.resolve("${name}.d.kt")

        assertEquals(
                compile(fileNameSource, translator),
                fileNameTarget.readText().trimEnd()
        )
    }

}