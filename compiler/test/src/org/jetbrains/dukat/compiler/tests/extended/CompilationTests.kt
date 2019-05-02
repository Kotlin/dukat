package org.jetbrains.dukat.compiler.tests.extended

import org.jetbrains.dukat.compiler.createGraalTranslator
import org.jetbrains.dukat.compiler.tests.OutputTests
import org.jetbrains.dukat.translator.InputTranslator
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource


@EnabledIfSystemProperty(named = "dukat.test.jsbackend.graal", matches = "true")
class CompilationTests : OutputTests() {

    override fun getTranslator(): InputTranslator = translator

    @DisplayName("core test set compile")
    @ParameterizedTest(name = "{0}")
    @MethodSource("extendedSet")
    @EnabledIfSystemProperty(named = "dukat.test.extended", matches = "true")
    fun withValueSourceCompiled(name: String, tsPath: String, ktPath: String) {
        assertContentCompiles(name, tsPath)
    }

    companion object {
        val translator: InputTranslator = createGraalTranslator()

        @JvmStatic
        fun extendedSet(): Array<Array<String>> {
            return fileSetWithDescriptors(System.getProperty("dukat.test.resources.definitelyTyped"))
        }

    }
}