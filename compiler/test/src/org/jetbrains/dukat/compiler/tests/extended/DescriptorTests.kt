package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.compiler.tests.FileFetcher
import org.jetbrains.dukat.compiler.tests.OutputTests
import org.jetbrains.dukat.compiler.tests.core.Idl2KtTests
import org.jetbrains.dukat.compiler.tests.descriptors.DescriptorValidator
import org.jetbrains.dukat.compiler.tests.descriptors.DescriptorValidator.validate
import org.jetbrains.dukat.compiler.tests.descriptors.RecursiveDescriptorComparator
import org.jetbrains.dukat.compiler.tests.descriptors.generatePackageDescriptor
import org.jetbrains.dukat.compiler.translator.IdlInputTranslator
import org.jetbrains.dukat.descriptors.translateToDescriptors
import org.jetbrains.dukat.idlReferenceResolver.DirectoryReferencesResolver
import org.jetbrains.dukat.idlReferenceResolver.EmptyReferencesResolver
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.translatorString.WEBIDL_DECLARATION_EXTENSION
import org.jetbrains.kotlin.name.FqName
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
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
        val sourceSet = IdlInputTranslator(EmptyReferencesResolver())
            .translate(tsPath)
        validate(
            DescriptorValidator.ValidationVisitor.errorTypesForbidden(), sourceSet.translateToDescriptors().getPackage(
                FqName("")
            )
        )
        val desc = generatePackageDescriptor(File(ktPath).parentFile.path, File(ktPath).name)
        assertEquals(
            RecursiveDescriptorComparator(RecursiveDescriptorComparator.RECURSIVE_ALL)
                .serializeRecursively(
                    sourceSet.translateToDescriptors().getPackage(
                        FqName("")
                    )
                ), RecursiveDescriptorComparator(RecursiveDescriptorComparator.RECURSIVE_ALL)
                .serializeRecursively(desc)
        )
    }

    override fun getTranslator() = translator

    companion object : FileFetcher() {
        override val postfix = WEBIDL_DECLARATION_EXTENSION

        @JvmStatic
        fun descriptorsTestSet(): Array<Array<String>> {
            return Idl2KtTests.fileSetWithDescriptors("./test/data/idl")
        }

        val translator: InputTranslator<String> = IdlInputTranslator(DirectoryReferencesResolver())
    }

}