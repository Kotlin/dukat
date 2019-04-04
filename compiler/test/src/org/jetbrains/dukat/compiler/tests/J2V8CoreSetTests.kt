package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.compiler.createV8Translator
import org.jetbrains.dukat.translator.InputTranslator

class J2V8CoreSetTests : GenerateCoreSetTests() {
    override fun getTranslator(): InputTranslator = translator

    companion object {
        val translator: InputTranslator = createV8Translator()
    }
}