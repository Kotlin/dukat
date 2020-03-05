package org.jetbrains.dukat.compiler.tests

import kotlinx.serialization.UnstableDefault
import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.cli.translateBinaryBundle
import org.jetbrains.dukat.compiler.tests.httpService.CliHttpClient
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.ts.translator.JsRuntimeByteArrayTranslator
import org.jetbrains.dukat.ts.translator.TypescriptLowerer


@UseExperimental(UnstableDefault::class)
class CliTranslator() {

    companion object {
        val HTTP_CLIENT_PORT = "8090"
    }

    private fun translateBinary(input: String) = CliHttpClient(HTTP_CLIENT_PORT).translate(input)

    fun translateBundle(
            input: String
    ): SourceBundleModel {
        val binData = translateBinary(input)
        val translator = JsRuntimeByteArrayTranslator(TypescriptLowerer(CommonJsNameResolver(), null))
        return translator.translate(binData)
    }

    fun translate(
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


@UseExperimental(UnstableDefault::class)
fun createStandardCliTranslator(): CliTranslator {
    return CliTranslator()
}