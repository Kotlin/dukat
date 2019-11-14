package org.jetbrains.dukat.js.type.export_resolution

import org.jetbrains.dukat.js.type.property_owner.Scope
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration

interface ExportResolver {
    fun resolve(scope: Scope) : List<TopLevelDeclaration>
}