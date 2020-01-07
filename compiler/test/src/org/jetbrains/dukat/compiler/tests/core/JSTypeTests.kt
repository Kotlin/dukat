package org.jetbrains.dukat.compiler.tests.core

import org.jetbrains.dukat.compiler.tests.BundleTranslator
import org.jetbrains.dukat.compiler.tests.FileFetcher
import org.jetbrains.dukat.compiler.tests.OutputTests
import org.jetbrains.dukat.compiler.tests.core.TestConfig.CONVERTER_SOURCE_PATH
import org.jetbrains.dukat.compiler.tests.core.TestConfig.DEFAULT_LIB_PATH
import org.jetbrains.dukat.compiler.tests.core.TestConfig.NODE_PATH
import org.jetbrains.dukat.js.translator.JavaScriptLowerer
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.panic.PanicMode
import org.jetbrains.dukat.panic.setPanicMode
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.translatorString.TS_DECLARATION_EXTENSION
import org.jetbrains.dukat.translatorString.translateModule
import org.jetbrains.dukat.ts.translator.JsRuntimeFileTranslator
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import kotlin.test.assertEquals

class JSTypeTests : OutputTests() {

    @DisplayName("js type test set")
    @ParameterizedTest(name = "{0}")
    @MethodSource("jsSet")
    fun withValueSource(name: String, jsPath: String, ktPath: String) {
        assertContentEqualsBinary(name, jsPath, ktPath)
    }

    //Never used
    override fun getTranslator(): InputTranslator<String> = JsRuntimeFileTranslator(JavaScriptLowerer(ConstNameResolver()), CONVERTER_SOURCE_PATH, DEFAULT_LIB_PATH, NODE_PATH)

    companion object : FileFetcher() {

        private val bundle = BundleTranslator("./build/javascript/declarations.dukat", JavaScriptLowerer(ConstNameResolver()))

        override val postfix = TS_DECLARATION_EXTENSION

        @JvmStatic
        fun jsSet(): Array<Array<String>> {
            return fileSetWithDescriptors("./test/data/javascript")
        }

        @JvmStatic
        @BeforeAll
        fun setup() {
            setPanicMode(PanicMode.NEVER_FAIL)
        }
    }


    private fun assertContentEqualsBinary(
            descriptor: String,
            tsPath: String,
            ktPath: String
    ) {
        setPanicMode(PanicMode.NEVER_FAIL)

        val targetShortName = "${descriptor}.d.kt"

        val modules = translateModule(bundle.translate(tsPath))
        val translated = concatenate(tsPath, modules)

        assertEquals(
                translated,
                File(ktPath).readText().trimEnd(),
                "\nSOURCE:\tfile:///${tsPath}\nTARGET:\tfile:///${ktPath}"
        )

        val outputDirectory = File("./build/tests/out")
        translated.let {
            val outputFile = outputDirectory.resolve(targetShortName)
            outputFile.parentFile.mkdirs()
            outputFile.writeText(translated)
        }
    }

}

