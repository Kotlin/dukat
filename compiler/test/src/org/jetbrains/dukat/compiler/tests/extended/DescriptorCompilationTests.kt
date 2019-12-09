package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.flattenDeclarations
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.ts.translator.createJsByteArrayTranslator
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class DescriptorCompilationTests : CompilationTests() {

    @DisplayName("descriptor compilation set")
    @ParameterizedTest(name = "{0}")
    @MethodSource("descriptorCompilationSet")
    @EnabledIfSystemProperty(named = "dukat.test.descriptorCompilation", matches = "true")
    override fun runTests(descriptor: String, sourcePath: String) {
        val sourceSet = createJsByteArrayTranslator(CommonJsNameResolver()).translate(
            File(sourcePath).readBytes()
        ).sources[0]
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
        println(flattenedSourceSet)
    }

    companion object {
        private val bundle = ("./build/declarations.dukat")

        @JvmStatic
        fun descriptorCompilationSet(): Array<Array<String>> = DefinitelyTypedCompilationTests.extendedSet()
    }

}