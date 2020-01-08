package org.jetbrains.dukat.js.type.exportResolution

import org.jetbrains.dukat.js.type.propertyOwner.Scope
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration

interface TypeAnalysisContext {
    fun getEnvironment(): Scope

    fun getExportsFrom(environment: Scope, defaultExportName: String) : List<TopLevelDeclaration>
}
