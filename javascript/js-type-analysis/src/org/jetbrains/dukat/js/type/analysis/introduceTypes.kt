package org.jetbrains.dukat.js.type.analysis

import org.jetbrains.dukat.js.type.property_owner.Scope
import org.jetbrains.dukat.js.type.export_resolution.GeneralExportResolver
import org.jetbrains.dukat.js.type.export_resolution.ExportResolver
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration

fun ModuleDeclaration.introduceTypes(exportResolver: ExportResolver) : ModuleDeclaration {
    val scope = Scope(null)
    val pathWalker = PathWalker()
    calculateConstraints(scope, pathWalker)

    if (pathWalker.startNextPath()) {
        //TODO check other paths
        raiseConcern("Conditional at top level not allowed!") {  }
    }

    val declarations = exportResolver.resolve(scope)
    return copy(declarations = declarations)
}

fun SourceFileDeclaration.introduceTypes(exportResolver: ExportResolver) = copy(root = root.introduceTypes(exportResolver))

fun SourceSetDeclaration.introduceTypes(exportResolver: ExportResolver = GeneralExportResolver()) = copy(sources = sources.map { it.introduceTypes(exportResolver) })