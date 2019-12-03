package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.compiler.tests.core.TestConfig
import org.jetbrains.dukat.compiler.tests.core.TestConfig.DEFINITELY_TYPED_DIR
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File


private fun File.isDefinitelyTypedDeclaration(): Boolean {
    return isFile() && name == "index.d.ts"
}

private fun File.getTestDescriptorName(): String {
    return parentFile.relativeTo(File(TestConfig.DEFINITELY_TYPED_DIR)).path
}

class DefinitelyTypedCompilationTests : CompilationTests() {
    @DisplayName("core test set compile")
    @ParameterizedTest(name = "{0}")
    @MethodSource("extendedSet")
    @EnabledIfSystemProperty(named = "dukat.test.extended", matches = "true")
    override fun runTests(
            descriptor: String,
            sourcePath: String
    ) {
        assertContentCompiles(descriptor, sourcePath)
    }

    companion object {
        @JvmStatic
        fun extendedSet(): Array<Array<String>> {
            val filterFunc: (File) -> Boolean = if (System.getProperty("dukat.test.definitelyTyped.repexp") == null) {
                File::isDefinitelyTypedDeclaration
            } else {
                val nameRegex = Regex(System.getProperty("dukat.test.definitelyTyped.repexp"), RegexOption.IGNORE_CASE)
                ({ file: File -> file.isDefinitelyTypedDeclaration() && nameRegex.matches(file.getTestDescriptorName()) })
            }

            val files = File(DEFINITELY_TYPED_DIR).walk()
                    .filter(filterFunc)
                    .map {
                        arrayOf(
                                it.getTestDescriptorName(),
                                it.absolutePath
                        )
                    }.toList().toTypedArray()

            return files
        }

    }
}