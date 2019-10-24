package org.jetbrains.dukat.compiler.tests.core

import org.jetbrains.dukat.compiler.tests.CliTranslator
import org.jetbrains.dukat.compiler.tests.core.TestConfig.DEFINITELY_TYPED_DIR
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import kotlin.test.assertEquals

class NpmCliTests {

    @DisplayName("cli test set")
    @ParameterizedTest(name = "{0}")
    @MethodSource("testSet")
    fun withValueSource(descriptor: String) {
        val sourceDir = File(DEFINITELY_TYPED_DIR, descriptor)
        val sourcePath = File(sourceDir, "index.d.ts")


        val targetPath = File("./build/test/data/cli/${descriptor}")

        targetPath.deleteRecursively()

        getTranslator().translate(sourcePath.absolutePath, targetPath.absolutePath, "./build/reports/CoreSetTestsCli/${descriptor}.json")

        val genFilesWalker = targetPath.walk()


        genFilesWalker.filter { it.isFile() }.forEach {
            println(it.absolutePath)
            assertGeneratedContent("${descriptor}/${it.toRelativeString(targetPath)}", it.absolutePath)
        }
    }

    companion object {
        fun getTranslator(): CliTranslator = CliTranslator(
                "../node-package/build/env.json",
                "../node-package/build/distrib/bin/dukat-cli.js"
        )

        @JvmStatic
        fun testSet(): Array<Array<String>> {
            val packages = listOf(
                    "request",
                    "universal-cookie"
            )

            return packages
                    .map { descriptor -> arrayOf(descriptor) }
                    .toTypedArray()
        }
    }

    protected fun assertGeneratedContent(generatedFileName: String, sourceFile: String) {
        val refPath = File("./test/data/cli")
        val refFile = File(refPath, generatedFileName).normalize()
        assertEquals(refFile.readText(), File(sourceFile).readText(), "\nreference: ${refFile.absolutePath} \nactual: $sourceFile")
    }

}

