package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.cli.translateWithBodyBinaryBundle
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
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
            moduleNameResolver: ModuleNameResolver,
            tsConfig: String?,
            dirName: String?,
            withDescriptors: Boolean,
            reportPath: String?
    ) {
        val binData = translateBinary(input, tsConfig)

        translateWithBodyBinaryBundle(binData, dirName, moduleNameResolver, null, reportPath)
    }
}