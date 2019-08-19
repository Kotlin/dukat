package org.jetbrains.dukat.compiler.tests.core

import org.jetbrains.dukat.compiler.tests.Bundle
import org.jetbrains.dukat.compiler.tests.FileFetcher
import org.jetbrains.dukat.compiler.tests.OutputTests
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME
import org.jetbrains.dukat.translatorString.TS_DECLARATION_EXTENSION
import org.jetbrains.dukat.translatorString.translateModule
import org.jetbrains.dukat.ts.translator.createJsFileTranslator
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import kotlin.test.assertEquals

class CoreSetTests : OutputTests() {

    @DisplayName("core test set")
    @ParameterizedTest(name = "{0}")
    @MethodSource("coreSet")
    fun withValueSource(name: String, tsPath: String, ktPath: String) {
        assertContentEqualsBinary(name, tsPath, ktPath)
    }

    override fun getTranslator(): InputTranslator<String> = translator

    companion object : FileFetcher() {

        private val bundle = Bundle("./build/declarations.dukat")

        override val postfix = TS_DECLARATION_EXTENSION

        @JvmStatic
        fun coreSet(): Array<Array<String>> {
            return fileSetWithDescriptors("./test/data")
        }

        val translator = createJsFileTranslator(ROOT_PACKAGENAME, ConstNameResolver(), SOURCE_PATH, DEFAULT_LIB_PATH, NODE_PATH)
    }


    protected fun assertContentEqualsBinary(
            descriptor: String,
            tsPath: String,
            ktPath: String
    ) {

        val targetShortName = "${descriptor}.d.kt"

        val modules = translateModule(bundle.translate(tsPath))
        val translated = concatenate(tsPath, modules)

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

