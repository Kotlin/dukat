package org.jetbrains.dukat.compiler.tests

import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.moduleNameResolver.ConstNameResolver
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME
import org.jetbrains.dukat.ts.translator.createJsByteArrayTranslator
import org.jetbrains.dukat.tsmodel.SourceBundleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import java.io.File

class Bundle(private val inputName: String) {

    companion object {
        val byteTranslator = createJsByteArrayTranslator(ROOT_PACKAGENAME, ConstNameResolver())
    }

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