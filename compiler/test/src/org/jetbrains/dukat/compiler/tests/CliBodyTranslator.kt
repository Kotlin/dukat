package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.cli.compileUnits
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.translatorString.translateByteArray
import org.jetbrains.dukat.ts.translator.createJsByteArrayWithBodyTranslator

class CliBodyTranslator : CliTranslator() {
    override val translator = createJsByteArrayWithBodyTranslator(CommonJsNameResolver(), null)

    override fun convert(
            input: String,
            tsConfig: String?,
            dirName: String,
            reportPath: String?
    ) {
        val binData = translateBinary(input, tsConfig)
        val translatedUnits = translateByteArray(binData, translator)
        compileUnits(translatedUnits, dirName, reportPath)
    }
}