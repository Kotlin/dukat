package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.flattenDeclarations
import org.jetbrains.dukat.compiler.tests.CliTranslator
import org.jetbrains.dukat.compiler.tests.MethodSourceSourceFiles
import org.jetbrains.dukat.compiler.tests.descriptors.DescriptorValidator
import org.jetbrains.dukat.compiler.tests.descriptors.DescriptorValidator.validate
import org.jetbrains.dukat.compiler.tests.descriptors.RecursiveDescriptorComparator
import org.jetbrains.dukat.compiler.tests.descriptors.generateModuleDescriptor
import org.jetbrains.dukat.descriptors.translateToDescriptors
import org.jetbrains.dukat.translatorString.D_TS_DECLARATION_EXTENSION
import org.jetbrains.dukat.translatorString.compileUnits
import org.jetbrains.dukat.translatorString.translateSourceSet
import org.jetbrains.kotlin.name.FqName
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import kotlin.test.assertEquals

internal fun assertDescriptorEquals(name: String, tsPath: String, tsConfig: String?) {
    val sourceSet = CliTranslator().translate(tsPath, tsConfig)

    val targetPath = "./build/test/data/descriptors/$name"
    File(targetPath).deleteRecursively()
    compileUnits(translateSourceSet(sourceSet), "./build/test/data/descriptors/$name")

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

    val outputModuleDescriptor = flattenedSourceSet.translateToDescriptors()
    val expectedModuleDescriptor =
        generateModuleDescriptor(File(targetPath).walk().filter { it.isFile }.toList())

    validate(
        DescriptorValidator.ValidationVisitor.errorTypesAllowed(),
        outputModuleDescriptor.getPackage(FqName.ROOT)
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

@ExtendWith(CliTestsStarted::class, CliTestsEnded::class)
class DescriptorTests {

    @DisplayName("descriptors test set")
    @ParameterizedTest(name = "{0}")
    @MethodSource("descriptorsTestSet")
    @Suppress("UNUSED_PARAMETER")
    fun withValueSource(name: String, tsPath: String, ktPath: String, tsConfig: String) {
        assertDescriptorEquals(name, tsPath, if (tsConfig.isEmpty()) null else tsConfig)
    }

    companion object {
        private val skippedDescriptorTests = setOf(
                "class/inheritance/overrides",
                "class/inheritance/overridesFromReferencedFile",
                "class/inheritance/overridingStdLib",
                "class/inheritance/simple",
                "interface/inheritance/simple",
                "interface/inheritance/withQualifiedParent",
                "mergeDeclarations/moduleWith/functionAndSecondaryWithTrait",
                "misc/missedOverloads"
        ).map { it.replace("/", System.getProperty("file.separator")) }

        @JvmStatic
        fun descriptorsTestSet(): Array<Array<String>> {
            return MethodSourceSourceFiles("./test/data/typescript/node_modules", D_TS_DECLARATION_EXTENSION)
                .fileSetWithDescriptors().filter { !skippedDescriptorTests.contains(it.first()) }.toTypedArray()
        }
    }
}
