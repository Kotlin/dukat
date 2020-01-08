package org.jetbrains.dukat.js.type.exportResolution

import org.jetbrains.dukat.js.type.constraint.resolution.toDeclarations
import org.jetbrains.dukat.js.type.propertyOwner.Scope
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration

class BasicJSContext : TypeAnalysisContext {
    override fun getEnvironment(): Scope {
        return Scope(null)
    }

    override fun getExportsFrom(environment: Scope, defaultExportName: String) : List<TopLevelDeclaration> {
        return environment.propertyNames.flatMap { propertyName ->
            val resolvedConstraint = environment[propertyName]
            resolvedConstraint.toDeclarations(propertyName)
        }
    }
}
