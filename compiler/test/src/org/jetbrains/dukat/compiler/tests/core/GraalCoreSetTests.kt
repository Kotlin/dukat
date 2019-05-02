package org.jetbrains.dukat.compiler.tests.core

import org.jetbrains.dukat.compiler.createGraalTranslator
import org.jetbrains.dukat.translator.InputTranslator
import org.junit.jupiter.api.condition.EnabledIfSystemProperty


@EnabledIfSystemProperty(named = "dukat.test.jsbackend.graal", matches = "true")
class GraalCoreSetTests : CoreSetTests() {

    override fun getTranslator(): InputTranslator = translator

    companion object {
        val translator: InputTranslator = createGraalTranslator()
    }

}