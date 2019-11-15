package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.ts.translator.createJsByteArrayTranslator
import java.io.File


typealias BundleSourceMap = Map<String, SourceSetModel>

class BundleTranslator(private val inputName: String) {

    companion object {
        val byteTranslator = createJsByteArrayTranslator(ConstNameResolver())
    }

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