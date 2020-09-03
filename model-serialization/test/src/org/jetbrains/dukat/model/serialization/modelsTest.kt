package org.jetbrains.dukat.model.serialization

import org.jetbrains.dukat.translator.ModuleTranslationUnit
import org.jetbrains.dukat.translatorString.translateModule
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class ModelsProtoTest {
    @Test
    fun test() {
        val binary = serializeStdLib("../stdlib-generator/build/libs/kotlin-stdlib-js.jar")
        val sourceSetConverted = convertProtobufToModels(binary)

        val units = translateModule(sourceSetConverted)

        val contents = units.filterIsInstance(ModuleTranslationUnit::class.java).flatMap {
            listOf("// --------- ${it.packageName} ---------", it.content)
        }.joinToString("\n")

        assertEquals(contents.trimEnd(), File("test/resources/code.out.kt").readText().trimEnd(), "De-serialized stdlib does not look like as expected")
    }
}