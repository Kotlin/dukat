package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.ts.translator.createJsByteArrayTranslator
import java.io.File


private data class BundleTranslatorInit(
        val sourceMap: Map<String, SourceSetModel>
)

class BundleTranslator(private val inputName: String) {

    companion object {
        val byteTranslator = createJsByteArrayTranslator(ConstNameResolver())
    }

    private val myInitData = build()

    private fun build(): BundleTranslatorInit {
        val bundleMap = mutableMapOf<String, SourceSetModel>()

        val inputStream = File(inputName).inputStream()
        val bundle = byteTranslator.lower(byteTranslator.parse(inputStream.readBytes()))

        bundle.sources.forEach { sourceSet ->
            bundleMap[sourceSet.sourceName] = sourceSet
        }

        return BundleTranslatorInit(bundleMap)
    }

    fun translate(fileName: String): SourceSetModel {
        val (sourceMap) = myInitData
        return sourceMap[fileName]!!
    }
}