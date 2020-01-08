package org.jetbrains.dukat.compiler.tests

import kotlinx.serialization.UnstableDefault
import org.jetbrains.dukat.cli.translateBinaryBundle
import org.jetbrains.dukat.compiler.tests.httpService.CliHttpClient
import org.jetbrains.dukat.js.translator.JavaScriptLowerer
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.ts.translator.ECMAScriptLowerer
import org.jetbrains.dukat.ts.translator.TypescriptLowerer


@UseExperimental(UnstableDefault::class)
class CliTranslator(private val nodeResolver: NodeResolver, private val translatorPath: String) {

    private fun getModuleNameResolver(moduleName: String?) = if (moduleName == null) {
        CommonJsNameResolver()
    } else {
        ConstNameResolver(moduleName)
    }

    private fun translate(
            input: String,
            dirName: String,
            reportPath: String? = null,
            lowerer: ECMAScriptLowerer
    ) {
        val binData = CliHttpClient("8090").translate(input)

        translateBinaryBundle(binData, dirName, lowerer, reportPath)
    }

    fun translateTS(
            input: String,
            dirName: String,
            reportPath: String? = null,
            moduleName: String? = null
    ) = translate(
            input,
            dirName,
            reportPath,
            TypescriptLowerer(getModuleNameResolver(moduleName))
    )

    fun translateJS(
            input: String,
            dirName: String,
            reportPath: String? = null,
            moduleName: String? = null
    ) = translate(
            input,
            dirName,
            reportPath,
            JavaScriptLowerer(getModuleNameResolver(moduleName))
    )
}


fun createStandardCliTranslator(): CliTranslator {
    return CliTranslator(
            NodeResolver("../node-package/build/env.json"),
            "../node-package/build/distrib/bin/dukat-cli.js"
    )
}