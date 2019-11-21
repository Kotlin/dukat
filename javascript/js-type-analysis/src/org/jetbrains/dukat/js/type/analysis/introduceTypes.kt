package org.jetbrains.dukat.js.type.analysis

import org.jetbrains.dukat.js.type.export_resolution.CommonJSEnvironmentProvider
import org.jetbrains.dukat.js.type.export_resolution.CommonJSExportResolver
import org.jetbrains.dukat.js.type.export_resolution.EnvironmentProvider
import org.jetbrains.dukat.js.type.export_resolution.ExportResolver
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration

fun ModuleDeclaration.introduceTypes(environmentProvider: EnvironmentProvider, exportResolver: ExportResolver) : ModuleDeclaration {
    val env = environmentProvider.getEnvironment()
    val pathWalker = PathWalker()
    calculateConstraints(env, pathWalker)

    if (pathWalker.startNextPath()) {
        //TODO check other paths
        raiseConcern("Conditional at top level not allowed!") {  }
    }

    val declarations = exportResolver.resolve(env)
    return copy(declarations = declarations)
}

fun SourceFileDeclaration.introduceTypes(environmentProvider: EnvironmentProvider, exportResolver: ExportResolver): SourceFileDeclaration {
    return copy(root = root.introduceTypes(environmentProvider, exportResolver))
}

fun SourceSetDeclaration.introduceTypes(environmentProvider: EnvironmentProvider = CommonJSEnvironmentProvider(), exportResolver: ExportResolver = CommonJSExportResolver()): SourceSetDeclaration {
//fun SourceSetDeclaration.introduceTypes(environmentProvider: EnvironmentProvider = EmptyEnvironmentProvider(), exportResolver: ExportResolver = GeneralExportResolver()): SourceSetDeclaration {
    return copy(sources = sources.map { it.introduceTypes(environmentProvider, exportResolver) })
}