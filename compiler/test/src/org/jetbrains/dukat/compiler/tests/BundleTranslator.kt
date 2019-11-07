package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.ts.translator.ECMAScriptLowerer
import org.jetbrains.dukat.ts.translator.JsRuntimeByteArrayTranslator
import org.jetbrains.dukat.ts.translator.TypescriptLowerer
import org.jetbrains.dukat.ts.translator.createJsByteArrayTranslator
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import java.io.File

class BundleTranslator(
        private val inputName: String,
        lowerer: ECMAScriptLowerer = TypescriptLowerer(ConstNameResolver())
) {
    private val byteTranslator = JsRuntimeByteArrayTranslator(lowerer)

    private val myMap = build()

    private fun build(): Map<String, SourceSetDeclaration> {
        val bundleMap = mutableMapOf<String, SourceSetDeclaration>()

        val inputStream = File(inputName).inputStream()
        val bundle = byteTranslator.parse(inputStream.readBytes())
        bundle.sources.forEach { sourceSet ->
            bundleMap.set(sourceSet.sourceName, sourceSet)
        }

        return bundleMap
    }

    fun translate(fileName: String): SourceSetModel {
        val sourceSet = myMap[fileName]!!
        return byteTranslator.lower(sourceSet)
    }
}