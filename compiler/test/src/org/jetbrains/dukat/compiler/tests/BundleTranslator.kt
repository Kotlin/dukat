package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.ts.translator.ECMAScriptLowerer
import org.jetbrains.dukat.ts.translator.JsRuntimeByteArrayTranslator
import org.jetbrains.dukat.ts.translator.TypescriptLowerer
import java.io.File


typealias BundleSourceMap = Map<String, SourceSetModel>

class BundleTranslator(
        private val inputName: String,
        lowerer: ECMAScriptLowerer = TypescriptLowerer(ConstNameResolver())
) {
    private val byteTranslator = JsRuntimeByteArrayTranslator(lowerer)

    private val bundleSourceMap = build()

    private fun build(): BundleSourceMap {
        val inputStream = File(inputName).inputStream()
        val bundle = byteTranslator.lower(byteTranslator.parse(inputStream.readBytes()))

        return bundle.sources.associateBy { source -> source.sourceName }
    }

    fun translate(fileName: String): SourceSetModel {
        return bundleSourceMap[fileName]!!
    }
}