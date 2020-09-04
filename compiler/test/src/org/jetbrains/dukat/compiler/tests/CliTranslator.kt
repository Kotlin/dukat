package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.cli.translateSourceSet
import org.jetbrains.dukat.compiler.tests.core.TestConfig
import org.jetbrains.dukat.compiler.tests.httpService.CliHttpClient
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.ts.translator.ECMAScriptLowerer
import org.jetbrains.dukat.ts.translator.JsRuntimeByteArrayTranslator
import org.jetbrains.dukat.ts.translator.TypescriptLowerer

val ADD_SUPPRESS_ANNOTATIONS = !(System.getProperty("dukat.test.omitSuppressAnnotations") == "true")

open class CliTranslator(private val translator: ECMAScriptLowerer = TypescriptLowerer(CommonJsNameResolver(), null, ADD_SUPPRESS_ANNOTATIONS)): InputTranslator<String> {

    protected fun translateBinary(input: String, tsConfig: String?) = CliHttpClient(TestConfig.CLI_TEST_SERVER_PORT).translate(input, tsConfig)

    override fun translate(
        data: String
    ): SourceSetModel {
        val binData = translateBinary(data, null)
        val translator = JsRuntimeByteArrayTranslator(translator)
        return translator.translate(binData)
    }

    open fun translate(
        data: String,
        tsConfig: String?
    ): SourceSetModel {
        val binData = translateBinary(data, tsConfig)
        val translator = JsRuntimeByteArrayTranslator(translator)
        return translator.translate(binData)
    }

    open fun translate(
            input: String,
            dirName: String,
            reportPath: String? = null,
            moduleName: String? = null,
            withDescriptors: Boolean = false,
            tsConfig: String? = null
    ) {

        val binData = translateBinary(input, tsConfig)

        val moduleNameResolver = if (moduleName == null) {
            CommonJsNameResolver()
        } else {
            ConstNameResolver(moduleName)
        }

        val translator = JsRuntimeByteArrayTranslator(TypescriptLowerer(moduleNameResolver, null, ADD_SUPPRESS_ANNOTATIONS))
        translateSourceSet(translator.translate(binData), dirName, reportPath, withDescriptors)
    }
}