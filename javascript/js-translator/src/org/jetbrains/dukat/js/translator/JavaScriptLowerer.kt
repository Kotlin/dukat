package org.jetbrains.dukat.js.translator

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.js.type.analysis.introduceTypes
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.ts.translator.TypescriptLowerer
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration

class JavaScriptLowerer(nameResolver: ModuleNameResolver) : TypescriptLowerer(nameResolver) {
    override fun lower(sourceSet: SourceSetDeclaration): SourceSetModel {
        return super.lower(sourceSet
                .introduceTypes()
        )
    }
}