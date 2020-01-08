package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.tsmodel.SourceBundleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetrbains.dukat.nodeLowering.lowerings.FqNode

interface ECMAScriptLowerer {
    fun lower(sourceSet: SourceSetDeclaration, stdLibSourceSet: SourceSetModel?, renameMap: Map<String, NameEntity>, uidToFqNameMapper: MutableMap<String, FqNode>): SourceSetModel
    fun lower(sourceBundle: SourceBundleDeclaration): SourceBundleModel
}