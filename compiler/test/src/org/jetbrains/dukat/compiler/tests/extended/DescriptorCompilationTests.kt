package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.compiler.tests.core.CoreSetCliTests
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
        println("file:///${sourcePath}")
        val targetPath = "./build/tests/descriptors/$START_TIMESTAMP/$descriptor"
        val targetDir = File(targetPath)
        println("file:///${targetDir.normalize().absolutePath}")

        targetDir.deleteRecursively()
        getTranslator().translate(sourcePath, targetPath, withDescriptors = true)
    }

    companion object {
        @JvmStatic
        fun descriptorCompilationSet(): Array<Array<String>> = CoreSetCliTests.fileSetWithDescriptors("./test/data/typescript")
    }

}