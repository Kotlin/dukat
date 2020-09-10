package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.ts.translator.createJsByteArrayWithBodyTranslator

class CliBodyTranslator : CliTranslator() {
    override val translator = createJsByteArrayWithBodyTranslator(CommonJsNameResolver(), null)
}