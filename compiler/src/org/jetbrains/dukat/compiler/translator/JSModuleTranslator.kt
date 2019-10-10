package org.jetbrains.dukat.compiler.translator

import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.commonLowerings.addStandardImportsAndAnnotations
import org.jetbrains.dukat.translator.InputTranslator
import org.jetbrains.dukat.js.lowerings.JSModuleFileLowerer
import org.jetbrains.dukat.js.parser.parseJS
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver


class JSModuleTranslator(private val moduleNameResolver: ModuleNameResolver): InputTranslator<String> {

    private fun translateFile(moduleName: String, fileName: String): SourceSetModel? {
        return JSModuleFileLowerer(parseJS(moduleName, fileName))
                .lower()
                .addStandardImportsAndAnnotations()
    }

    override fun translate(data: String): SourceBundleModel {
        val moduleName = moduleNameResolver.resolveName(data)

        return if (moduleName != null) {
            val sourceSetModel = translateFile(moduleName, data)

            if (sourceSetModel != null) {
                SourceBundleModel(listOf(sourceSetModel))
            } else {
                SourceBundleModel(listOf())
            }
        } else {
            throw IllegalArgumentException("Could not resolve module name.")
        }
    }

}