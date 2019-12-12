package org.jetbrains.dukat.js.type.export_resolution

import org.jetbrains.dukat.js.type.constraint.resolution.toDeclaration
import org.jetbrains.dukat.js.type.property_owner.Scope
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration

class BasicJSContext : TypeAnalysisContext {
    override fun getEnvironment(): Scope {
        return Scope(null)
    }

    override fun getExportsFrom(environment: Scope, defaultExportName: String) : List<TopLevelDeclaration> {
        return environment.propertyNames.mapNotNull { propertyName ->
            val resolvedConstraint = environment[propertyName]
            resolvedConstraint.toDeclaration(propertyName)
        }
    }
}
