package org.jetbrains.dukat.js.type.export_resolution

import org.jetbrains.dukat.js.type.constraint.property_owner.PropertyOwner
import org.jetbrains.dukat.js.type.constraint.resolution.toDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration

class GeneralExportResolver : ExportResolver {
    override fun resolve(owner: PropertyOwner) : List<TopLevelDeclaration> {
        return owner.propertyNames.mapNotNull { propertyName ->
            val resolvedConstraint = owner[propertyName]?.resolve(owner)
            resolvedConstraint?.toDeclaration(propertyName)
        }
    }
}