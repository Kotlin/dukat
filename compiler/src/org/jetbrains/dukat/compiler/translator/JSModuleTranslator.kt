package org.jetbrains.dukat.compiler.translator

import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.jsParser.parseJS
import org.jetbrains.dukat.translator.InputTranslator

class JSModuleTranslator(): InputTranslator<String> {

    private fun translateFile(path: String): SourceSetModel? {
        parseJS(path)
        return null
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