package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.compiler.createNashornTranslator
import org.jetbrains.dukat.translator.InputTranslator

class NashornCoreSetTests : GenerateCoreSetTests() {

    override fun getTranslator(): InputTranslator = translator

    companion object {
        val translator: InputTranslator = createNashornTranslator()
    }

}