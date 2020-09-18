package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.compiler.tests.core.TestConfig
import org.jetbrains.dukat.compiler.tests.httpService.CliHttpClient
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.translatorString.compileUnits
import org.jetbrains.dukat.translatorString.translateSourceSet
import org.jetbrains.dukat.ts.translator.translateTypescriptDeclarations

val ADD_SUPPRESS_ANNOTATIONS = !(System.getProperty("dukat.test.omitSuppressAnnotations") == "true")

open class CliTranslator : InputTranslator<String> {
    protected fun translateBinary(input: String, tsConfig: String?) = CliHttpClient(TestConfig.CLI_TEST_SERVER_PORT).translate(input, tsConfig)

    override fun translate(
        data: String
    ): SourceSetModel {
        return translate(data, null)
    }

    open fun translate(
            input: String,
            tsConfig: String? = null
    ): SourceSetModel {
        val binData = translateBinary(input, tsConfig)
        return translateTypescriptDeclarations(binData, CommonJsNameResolver(), null, ADD_SUPPRESS_ANNOTATIONS)
    }

    open fun convert(
            input: String,
            tsConfig: String? = null,
            dirName: String = "./"
    ): Iterable<String> {
        val sourceSet = translate(input, tsConfig)
        return compileUnits(translateSourceSet(sourceSet), dirName)
    }
}