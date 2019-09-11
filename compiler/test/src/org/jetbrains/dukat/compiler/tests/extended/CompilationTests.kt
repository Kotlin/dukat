package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.compiler.tests.CliTranslator
import org.jetbrains.dukat.compiler.tests.CompileMessageCollector
import org.jetbrains.dukat.compiler.tests.core.TestConfig.DEFINITELY_TYPED_DIR
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.arguments.K2JSCompilerArguments
import org.jetbrains.kotlin.cli.js.K2JSCompiler
import org.jetbrains.kotlin.config.Services
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import kotlin.test.assertEquals

class CompilationTests {

    private fun getTranslator(): CliTranslator = CliTranslator("../node-package/build/env.json", "../node-package/build/distrib/bin/dukat-cli.js")

    @DisplayName("core test set compile")
    @ParameterizedTest(name = "{0}")
    @MethodSource("extendedSet")
    @EnabledIfSystemProperty(named = "dukat.test.extended", matches = "true")
    fun withValueSourceCompiled(
            descriptor: String,
            sourcePath: String
    ) {
        assertContentCompiles(descriptor, sourcePath)
    }


    private fun compile(sourcePath: String, targetPath: String): ExitCode {

        val options =
                K2JSCompilerArguments().apply {
                    outputFile = targetPath
                    metaInfo = false
                    sourceMap = false
                    noStdlib = true
                    libraries = listOf(
                            "./build/kotlinHome/kotlin-stdlib-js.jar"
                    ).joinToString(File.pathSeparator)
                }

        options.freeArgs = listOf(sourcePath)

        return K2JSCompiler().exec(
                CompileMessageCollector(),
                Services.EMPTY,
                options
        )
    }


    private fun assertContentCompiles(
            descriptor: String,
            sourcePath: String
    ) {

        val targetPath = "./build/tests/compiled/${descriptor}"
        getTranslator().translate(sourcePath, targetPath)
        val targetSource = File(targetPath, "index.kt")
        val outSource = "${targetPath}/${descriptor}.js"

        assert(targetSource.exists()) { "$targetSource no found " }

        assertEquals(
                ExitCode.OK,
                compile(
                        targetSource.absolutePath,
                        outSource
                )
        )
    }

    companion object {

        @JvmStatic
        fun extendedSet(): Array<Array<String>> {
            val files = File(DEFINITELY_TYPED_DIR).walk()
                    .filter { it.isFile() && it.name == "index.d.ts" }
                    .map {
                        arrayOf(
                                it.parentFile.name,
                                it.absolutePath
                        )
                    }.toList().toTypedArray()

            return files
        }

    }
}


fun main() {

}