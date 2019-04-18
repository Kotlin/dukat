package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.compiler.createNashornTranslator
import org.jetbrains.dukat.translator.InputTranslator
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource



class NashornCoreSetTests : GenerateCoreSetTests() {

    override fun getTranslator(): InputTranslator = translator

    @DisplayName("core test set compile")
    @ParameterizedTest(name = "{0}")
    @MethodSource("extendedSet")
    @EnabledIfSystemProperty(named = "dukat.test.extended", matches = "true")
    fun withValueSourceCompiled(name: String, tsPath: String, ktPath: String) {
        assertContentCompiles(name, tsPath)
    }

    companion object {
        val translator: InputTranslator = createNashornTranslator()
    }

}