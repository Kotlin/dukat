package org.jetbrains.dukat.js.translator

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.js.type.analysis.introduceTypes
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.ts.translator.TypescriptLowerer
import org.jetbrains.dukat.tsLowerings.mergeDuplicates.mergeDuplicates
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetrbains.dukat.nodeLowering.lowerings.FqNode

class JavaScriptLowerer(nameResolver: ModuleNameResolver) : TypescriptLowerer(nameResolver) {
    override fun lower(sourceSet: SourceSetDeclaration, stdLibSourceSet: SourceSetModel?, renameMap: Map<String, NameEntity>, uidToFqNameMapper: MutableMap<String, FqNode>): SourceSetModel {
        return super.lower(
                sourceSet
                        .introduceTypes()
                        .mergeDuplicates(),
                stdLibSourceSet,
                renameMap,
                uidToFqNameMapper
        )
    }
}
