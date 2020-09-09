package org.jetbrains.dukat.js.translator

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.js.type.analysis.introduceTypes
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.ts.translator.ECMAScriptLowerer
import org.jetbrains.dukat.ts.translator.TypescriptLowerer
import org.jetbrains.dukat.tsLowerings.mergeDuplicates.mergeDuplicates
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration

class JavaScriptLowerer(nameResolver: ModuleNameResolver) : ECMAScriptLowerer {
    private val typescriptLowerer = TypescriptLowerer(nameResolver, null, true)

    override fun lower(sourceSet: SourceSetDeclaration): SourceSetModel {
        return typescriptLowerer.lower(
                sourceSet
                        .introduceTypes()
                        .mergeDuplicates()
        )
    }
}
