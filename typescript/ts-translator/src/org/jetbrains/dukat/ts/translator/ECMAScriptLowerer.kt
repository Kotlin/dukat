package org.jetbrains.dukat.ts.translator

import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.tsmodel.SourceBundleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration

interface ECMAScriptLowerer {
    fun lower(sourceSet: SourceSetDeclaration): SourceSetModel

    fun lower(sourceBundle: SourceBundleDeclaration): SourceBundleModel {
        return SourceBundleModel(sources = sourceBundle.sources.map {
            lower(it)
        })
    }
}