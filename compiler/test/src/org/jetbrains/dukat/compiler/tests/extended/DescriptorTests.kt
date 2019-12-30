package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.flattenDeclarations
import org.jetbrains.dukat.cli.compileUnits
import org.jetbrains.dukat.compiler.tests.BundleTranslator
import org.jetbrains.dukat.compiler.tests.FileFetcher
import org.jetbrains.dukat.compiler.tests.OutputTests
import org.jetbrains.dukat.compiler.tests.core.TestConfig
import org.jetbrains.dukat.compiler.tests.descriptors.DescriptorValidator
import org.jetbrains.dukat.compiler.tests.descriptors.DescriptorValidator.validate
import org.jetbrains.dukat.compiler.tests.descriptors.RecursiveDescriptorComparator
import org.jetbrains.dukat.compiler.tests.descriptors.generateModuleDescriptor
import org.jetbrains.dukat.descriptors.translateToDescriptors
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.translatorString.TS_DECLARATION_EXTENSION
import org.jetbrains.dukat.translatorString.translateModule
import org.jetbrains.dukat.ts.translator.JsRuntimeFileTranslator
import org.jetbrains.kotlin.name.FqName
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.io.OutputStream
import java.io.PrintStream
import kotlin.test.assertEquals

@Suppress("unused")
class DescriptorTests : OutputTests() {

    @DisplayName("descriptors test set")
    @ParameterizedTest(name = "{0}")
    @MethodSource("descriptorsTestSet")
    @EnabledIfSystemProperty(named = "dukat.test.descriptors", matches = "true")
    fun withValueSource(name: String, tsPath: String, ktPath: String) {
        assertDescriptorEquals(name, tsPath, ktPath)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun assertDescriptorEquals(name: String, tsPath: String, ktPath: String) {
        val sourceSet = bundle.translate(tsPath)
        val flattenedSourceSet = sourceSet.copy(sources = sourceSet.sources.flatMap { sourceFile ->
            sourceFile.root.flattenDeclarations().map {
                SourceFileModel(
                        sourceFile.name,
                        sourceFile.fileName,
                        it,
                        sourceFile.referencedFiles
                )
            }
        })

        val targetPath = "./build/test/data/descriptors/$name"
        File(targetPath).deleteRecursively()
        val outPrintStream = System.out
        System.setOut(PrintStream(object : OutputStream() {
            override fun write(b: Int) {

            }
        }))
        compileUnits(translateModule(sourceSet), "./build/test/data/descriptors/$name", null)
        System.setOut(outPrintStream)

        val outputModuleDescriptor = flattenedSourceSet.translateToDescriptors()
        val expectedModuleDescriptor =
                generateModuleDescriptor(File(targetPath).walk().filter { it.isFile }.toList())

        validate(
                DescriptorValidator.ValidationVisitor.errorTypesAllowed(), outputModuleDescriptor.getPackage(
                FqName.ROOT
        )
        )

        assertEquals(
                RecursiveDescriptorComparator(RecursiveDescriptorComparator.RECURSIVE_ALL)
                        .serializeRecursively(
                                outputModuleDescriptor.getPackage(FqName.ROOT)
                        ),
                RecursiveDescriptorComparator(RecursiveDescriptorComparator.RECURSIVE_ALL)
                        .serializeRecursively(expectedModuleDescriptor.getPackage(FqName.ROOT))
        )
    }

    override fun getTranslator() = translator

    companion object : FileFetcher() {
        private val bundle = BundleTranslator("./build/declarations.dukat")

        override val postfix = TS_DECLARATION_EXTENSION

        @JvmStatic
        fun descriptorsTestSet(): Array<Array<String>> {
            return fileSetWithDescriptors("./test/data/typescript/")
        }

        val translator: InputTranslator<String> = JsRuntimeFileTranslator(
                ConstNameResolver(),
                TestConfig.CONVERTER_SOURCE_PATH,
                TestConfig.DEFAULT_LIB_PATH,
                TestConfig.NODE_PATH
        )
    }

}