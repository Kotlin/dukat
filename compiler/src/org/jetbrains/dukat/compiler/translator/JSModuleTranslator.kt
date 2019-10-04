package org.jetbrains.dukat.compiler.translator

import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.translator.InputTranslator

class JSModuleTranslator(): InputTranslator<String> {

    override fun translate(data: String): SourceBundleModel {
        println(data)
        return SourceBundleModel(listOf())
    }

}