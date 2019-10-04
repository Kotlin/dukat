package org.jetbrains.dukat.compiler.translator

import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.jsParser.parseJS
import org.jetbrains.dukat.jsLowerings.convert
import org.jetbrains.dukat.astModel.*


class JSModuleTranslator: InputTranslator<String> {

    private fun translateFile(fileName: String): SourceSetModel? {
        return parseJS(fileName).convert()
    }

    override fun translate(data: String): SourceBundleModel {
        val sourceSetModel = translateFile(data)

        return if(sourceSetModel != null) {
            SourceBundleModel(listOf(sourceSetModel))
        } else {
            SourceBundleModel(listOf())
        }
    }

}