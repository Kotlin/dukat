package org.jetbrains.dukat.compiler.tests

import kotlinx.serialization.UnstableDefault
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.cli.translateBinaryBundle
import org.jetbrains.dukat.compiler.tests.httpService.CliHttpClient
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.ts.translator.ECMAScriptLowerer
import org.jetbrains.dukat.ts.translator.JsRuntimeByteArrayTranslator
import org.jetbrains.dukat.ts.translator.TypescriptLowerer


@OptIn(UnstableDefault::class)
open class CliTranslator(private val translator: ECMAScriptLowerer = TypescriptLowerer(CommonJsNameResolver(), null)): InputTranslator<String> {

    companion object {
        val HTTP_CLIENT_PORT = "8090"
    }

    protected fun translateBinary(input: String) = CliHttpClient(HTTP_CLIENT_PORT).translate(input)

    override fun translate(
        data: String
    ): SourceSetModel {
        val binData = translateBinary(data)
        val translator = JsRuntimeByteArrayTranslator(translator)
        return translator.translate(binData)
    }

    open fun translate(
            input: String,
            dirName: String,
            reportPath: String? = null,
            moduleName: String? = null,
            withDescriptors: Boolean = false
    ) {
        val binData = translateBinary(input)

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