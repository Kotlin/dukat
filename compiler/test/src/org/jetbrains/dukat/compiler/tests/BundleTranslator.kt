package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.ts.translator.ECMAScriptLowerer
import org.jetbrains.dukat.ts.translator.JsRuntimeByteArrayTranslator
import org.jetbrains.dukat.ts.translator.TypescriptLowerer
import org.jetbrains.dukat.translator.LIB_PACKAGENAME
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import java.io.File

private data class BundleTranslatorInit(
        val sourceMap: Map<String, SourceSetDeclaration>,
        val stdLib: SourceSetModel?
)

class BundleTranslator(
        private val inputName: String,
        lowerer: ECMAScriptLowerer = TypescriptLowerer(ConstNameResolver())
) {
    private val byteTranslator = JsRuntimeByteArrayTranslator(lowerer)

    private val myInitData = build()

    private fun build(): BundleTranslatorInit {
        val bundleMap = mutableMapOf<String, SourceSetDeclaration>()

        val inputStream = File(inputName).inputStream()
        val bundle = byteTranslator.parse(inputStream.readBytes())
        bundle.sources.forEach { sourceSet ->
            bundleMap[sourceSet.sourceName] = sourceSet
        }

        val stdLib = bundleMap[LIB_PACKAGENAME.value]?.let {
            byteTranslator.lower(it, null)
        }

        return BundleTranslatorInit(bundleMap, stdLib)
    }

    fun translate(fileName: String): SourceSetModel {
        val (sourceMap, stdLib) = myInitData
        return byteTranslator.lower(sourceMap[fileName]!!, stdLib)
    }
}