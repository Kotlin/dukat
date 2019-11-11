package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.translator.LIB_PACKAGENAME
import org.jetbrains.dukat.ts.translator.createJsByteArrayTranslator
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import java.io.File


private data class BundleTranslatorInit(
        val sourceMap: Map<String, SourceSetDeclaration>,
        val stdLib: SourceSetModel?
)

class BundleTranslator(private val inputName: String) {

    companion object {
        val byteTranslator = createJsByteArrayTranslator(ConstNameResolver())
    }

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