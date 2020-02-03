package org.jetbrains.dukat.compiler.tests

import kotlinx.serialization.UnstableDefault
import org.jetbrains.dukat.cli.translateBinaryBundle
import org.jetbrains.dukat.compiler.tests.httpService.CliHttpClient
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver


@UseExperimental(UnstableDefault::class)
class CliTranslator() {

    fun translate(
        input: String,
        dirName: String,
        reportPath: String? = null,
        moduleName: String? = null,
        withDescriptors: Boolean = false
    ) {
        //protobuf
        val binData = CliHttpClient("8090").translate(input)

        val moduleNameResolver = if (moduleName == null) {
            CommonJsNameResolver()
        } else {
            ConstNameResolver(moduleName)
        }

        translateBinaryBundle(binData, dirName, moduleNameResolver, null, reportPath, withDescriptors)
    }
}


@UseExperimental(UnstableDefault::class)
fun createStandardCliTranslator(): CliTranslator {
    return CliTranslator()
}