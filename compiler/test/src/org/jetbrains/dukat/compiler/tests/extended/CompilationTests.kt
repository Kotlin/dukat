package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.compiler.tests.CompileMessageCollector
import org.jetbrains.dukat.compiler.tests.FileFetcher
import org.jetbrains.dukat.compiler.tests.OutputTests
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.translator.ModuleTranslationUnit
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME
import org.jetbrains.dukat.translatorString.translateModule
import org.jetbrains.dukat.ts.translator.createJsFileTranslator
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

class CompilationTests : OutputTests() {

    override fun getTranslator(): InputTranslator<String> = translator

    @DisplayName("core test set compile")
    @ParameterizedTest(name = "{0}")
    @MethodSource("extendedSet")
    @EnabledIfSystemProperty(named = "dukat.test.extended", matches = "true")
    fun withValueSourceCompiled(
            name: String,
            tsPath: String,
            @Suppress("UNUSED_PARAMETER") ktPath: String
    ) {
        assertContentCompiles(name, tsPath)
    }


    private fun compile(sourcePath: String, targetPath: String): ExitCode {
        val options =
                K2JSCompilerArguments().apply {
                    outputFile = targetPath
                    metaInfo = false
                    sourceMap = false
                    kotlinHome = "./build"
                }

        options.freeArgs = listOf(sourcePath)
        return K2JSCompiler().exec(
                CompileMessageCollector(),
                Services.EMPTY,
                options
        )
    }


    private fun assertContentCompiles(
            @Suppress("UNUSED_PARAMETER") descriptor: String,
            tsPath: String
    ) {
        val translated = translateModule(tsPath, translator)
        val outputDirectory = File("./build/tests/out")
        outputDirectory.mkdirs()

        val sourceFile = File(tsPath)
        val targetDir =
                File(outputDirectory, sourceFile.parentFile.absolutePath.removePrefix(pathToTypes))

        val mainTargetName = sourceFile.name.removeSuffix(".d.ts") + ".kt"

        targetDir.mkdirs()

        val (successfullTranslations, failedTranslations) = translated.partition { it is ModuleTranslationUnit }

        if (failedTranslations.isNotEmpty()) {
            throw Exception("translation failed")
        }

        val units = successfullTranslations.filterIsInstance(ModuleTranslationUnit::class.java)

        units.forEach { (name, _, _, content) ->
            val targetName = "${name}.kt"
            val resolvedTarget = targetDir.resolve(targetName)

            resolvedTarget.writeText(content)
        }

        val targetPath = targetDir.resolve(mainTargetName).absolutePath
        assertEquals(ExitCode.OK,
                compile(
                        targetPath,
                        targetPath.replace(".kt", ".js")
                ))
    }

    companion object : FileFetcher() {

        override val postfix = ".d.ts"

        val translator: InputTranslator<String> = createJsFileTranslator(ROOT_PACKAGENAME, ConstNameResolver(), SOURCE_PATH, DEFAULT_LIB_PATH, NODE_PATH)
        val pathToTypes = System.getProperty("dukat.test.resources.definitelyTyped")

        @JvmStatic
        fun extendedSet(): Array<Array<String>> {
            return fileSetWithDescriptors(pathToTypes)
        }

    }
}