package org.jetbrains.dukat.compiler.tests.core

import org.jetbrains.dukat.compiler.createNashornTranslator
import org.jetbrains.dukat.translator.InputTranslator
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource


@EnabledIfSystemProperty(named = "dukat.test.jsbackend.nashorn", matches = "true")
class NashornCoreSetTests : CoreSetTests() {

    override fun getTranslator(): InputTranslator = translator

    companion object {
        val translator: InputTranslator = createNashornTranslator()
    }
}