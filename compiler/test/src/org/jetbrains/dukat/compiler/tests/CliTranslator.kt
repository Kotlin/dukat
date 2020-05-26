package org.jetbrains.dukat.compiler.tests

import kotlinx.serialization.UnstableDefault
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.cli.translateBinaryBundle
import org.jetbrains.dukat.compiler.tests.core.TestConfig
import org.jetbrains.dukat.compiler.tests.httpService.CliHttpClient
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.ts.translator.ECMAScriptLowerer
import org.jetbrains.dukat.ts.translator.JsRuntimeByteArrayTranslator
import org.jetbrains.dukat.ts.translator.TypescriptLowerer


@OptIn(UnstableDefault::class)
open class CliTranslator(private val translator: ECMAScriptLowerer = TypescriptLowerer(CommonJsNameResolver(), null)): InputTranslator<String> {

    protected fun translateBinary(input: String, tsConfig: String?) = CliHttpClient(TestConfig.CLI_TEST_SERVER_PORT).translate(input, tsConfig)

    override fun translate(
        data: String
    ): SourceSetModel {
        val binData = translateBinary(data, null)
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

        translateBinaryBundle(binData, dirName, JsRuntimeByteArrayTranslator(TypescriptLowerer(moduleNameResolver, null)), reportPath, withDescriptors)
    }
}


@OptIn(UnstableDefault::class)
fun createStandardCliTranslator(translator: ECMAScriptLowerer = TypescriptLowerer(CommonJsNameResolver(), null)): CliTranslator {
    return CliTranslator(translator)
}