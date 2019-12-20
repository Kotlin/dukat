package org.jetbrains.dukat.compiler.tests

import kotlinx.serialization.UnstableDefault
import org.jetbrains.dukat.cli.translateBinaryBundle
import org.jetbrains.dukat.compiler.tests.httpService.CliHttpClient
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.kotlin.backend.common.push


@UseExperimental(UnstableDefault::class)
class CliTranslator(private val nodeResolver: NodeResolver, private val translatorPath: String) {
    private fun createCliArgs(
            input: String,
            binaryOutput: Boolean = false,
            reportPath: String? = null,
            moduleName: String? = null
    ): Array<String> {
        val args = mutableListOf(
                nodeResolver.nodePath,
                translatorPath
        )

        if (reportPath != null) {
            args.push("-r")
            args.push(reportPath)
        }

        if (moduleName != null) {
            args.push("-m")
            args.push(moduleName)
        }

        if (binaryOutput) {
            args.push("-b")
        }

        args.push(input)
        return args.toTypedArray()
    }

    fun translate(
            input: String,
            dirName: String,
            reportPath: String? = null,
            moduleName: String? = null
    ) {
        val binData = CliHttpClient("8090").translate(input)

        val moduleNameResolver = if (moduleName == null) {
            CommonJsNameResolver()
        } else {
            ConstNameResolver(moduleName)
        }

        translateBinaryBundle(binData, dirName, moduleNameResolver, reportPath)
    }
}


fun createStandardCliTranslator(): CliTranslator {
    return CliTranslator(
            NodeResolver("../node-package/build/env.json"),
            "../node-package/build/distrib/bin/dukat-cli.js"
    )
}