package org.jetbrains.dukat.js.type.export_resolution

import org.jetbrains.dukat.js.type.constraint.resolution.toDeclaration
import org.jetbrains.dukat.js.type.property_owner.Scope
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration

class GeneralExportResolver : ExportResolver {
    override fun resolve(scope: Scope) : List<TopLevelDeclaration> {
        return scope.propertyNames.mapNotNull { propertyName ->
            val resolvedConstraint = scope[propertyName].resolve(scope)
            resolvedConstraint.toDeclaration(propertyName)
        }
    }
}