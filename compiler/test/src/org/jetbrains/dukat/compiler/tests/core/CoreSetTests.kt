package org.jetbrains.dukat.compiler.tests.core

import org.jetbrains.dukat.compiler.tests.BundleTranslator
import org.jetbrains.dukat.compiler.tests.FileFetcher
import org.jetbrains.dukat.compiler.tests.OutputTests
import org.jetbrains.dukat.compiler.tests.core.TestConfig.CONVERTER_SOURCE_PATH
import org.jetbrains.dukat.compiler.tests.core.TestConfig.DEFAULT_LIB_PATH
import org.jetbrains.dukat.compiler.tests.core.TestConfig.NODE_PATH
import org.jetbrains.dukat.compiler.tests.toFileUriScheme
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.translatorString.TS_DECLARATION_EXTENSION
import org.jetbrains.dukat.translatorString.translateModule
import org.jetbrains.dukat.ts.translator.JsRuntimeFileTranslator
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import kotlin.test.assertEquals

private fun createJsFileTranslator(moduleNameResolver: ModuleNameResolver, translatorPath: String, libPath: String, nodePath: String) = JsRuntimeFileTranslator(moduleNameResolver, translatorPath, libPath, nodePath)

class CoreSetTests : OutputTests() {

    @DisplayName("core test set")
    @ParameterizedTest(name = "{0}")
    @MethodSource("coreSet")
    fun withValueSource(name: String, tsPath: String, ktPath: String) {
        assertContentEqualsBinary(name, tsPath, ktPath)
    }

    override fun getTranslator(): InputTranslator<String> = translator

    companion object : FileFetcher() {

        private val bundle = BundleTranslator("./build/declarations.dukat")

        override val postfix = TS_DECLARATION_EXTENSION

        @JvmStatic
        fun coreSet(): Array<Array<String>> {
            return fileSetWithDescriptors("./test/data/typescript")
        }

        val translator = createJsFileTranslator(ConstNameResolver(), CONVERTER_SOURCE_PATH, DEFAULT_LIB_PATH, NODE_PATH)
    }


    protected fun assertContentEqualsBinary(
            descriptor: String,
            tsPath: String,
            ktPath: String
    ) {

        val targetShortName = "${descriptor}.d.kt"

        val modules = translateModule(bundle.translate(tsPath))
        val translated = concatenate(tsPath, modules)

        print("\nSOURCE:\t${tsPath.toFileUriScheme()}\nTARGET:\t${ktPath.toFileUriScheme()}")

        assertEquals(
                translated,
                File(ktPath).readText().trimEnd()
        )

        val outputDirectory = File("./build/tests/out")
        translated.let {
            val outputFile = outputDirectory.resolve(targetShortName)
            outputFile.parentFile.mkdirs()
            outputFile.writeText(translated)
        }
    }

}

