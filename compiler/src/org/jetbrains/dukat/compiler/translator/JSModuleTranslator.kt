package org.jetbrains.dukat.compiler.translator

import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.js.parser.parseJS
import org.jetbrains.dukat.js.lowerings.convert
import org.jetbrains.dukat.astModel.*
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver


class JSModuleTranslator(private val moduleNameResolver: ModuleNameResolver): InputTranslator<String> {

    private fun translateFile(moduleName: String, fileName: String): SourceSetModel? {
        return parseJS(moduleName, fileName).convert()
    }

    override fun translate(data: String): SourceBundleModel {
        val moduleName = moduleNameResolver.resolveName(data)

        return if(moduleName != null) {
            val sourceSetModel = translateFile(moduleName, data)

            if(sourceSetModel != null) {
                SourceBundleModel(listOf(sourceSetModel))
            } else {
                SourceBundleModel(listOf())
            }
        } else {
            //maybe throw error instead?
            SourceBundleModel(listOf())
        }
    }

}