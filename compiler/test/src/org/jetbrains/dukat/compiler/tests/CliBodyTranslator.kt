package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.ts.translator.createJsByteArrayWithBodyTranslator
import org.jetbrains.dukat.ts.translator.translateTypescriptDeclarations
import org.jetbrains.dukat.ts.translator.translateTypescriptSources

class CliBodyTranslator : CliTranslator() {
    override fun translate(input: String, tsConfig: String?): SourceSetModel {
        val binData = translateBinary(input, tsConfig)
        return translateTypescriptSources(binData, CommonJsNameResolver(), null)
    }
}