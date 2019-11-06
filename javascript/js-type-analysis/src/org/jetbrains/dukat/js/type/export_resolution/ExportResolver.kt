package org.jetbrains.dukat.js.type.export_resolution

import org.jetbrains.dukat.js.type.constraint.property_owner.PropertyOwner
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration

interface ExportResolver {
    fun resolve(owner: PropertyOwner) : List<TopLevelDeclaration>
}