package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.cli.compileUnits
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.translatorString.translateByteArray
import org.jetbrains.dukat.ts.translator.createJsByteArrayWithBodyTranslator

class CliBodyTranslator : CliTranslator() {
    override fun translate(
        data: String
    ): SourceSetModel {
        val binData = translateBinary(data, null)
        val translator = createJsByteArrayWithBodyTranslator(CommonJsNameResolver(), null)
        return translator.translate(binData)
    }

    override fun convert(
            input: String,
            tsConfig: String?,
            dirName: String,
            reportPath: String?
    ) {
        val binData = translateBinary(input, tsConfig)
        val translator = createJsByteArrayWithBodyTranslator(CommonJsNameResolver(), null)
        val translatedUnits = translateByteArray(binData, translator)
        compileUnits(translatedUnits, dirName, reportPath)
    }
}