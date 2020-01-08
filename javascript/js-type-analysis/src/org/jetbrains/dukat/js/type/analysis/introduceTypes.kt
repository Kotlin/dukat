package org.jetbrains.dukat.js.type.analysis

import org.jetbrains.dukat.js.type.exportResolution.CommonJSContext
import org.jetbrains.dukat.js.type.exportResolution.TypeAnalysisContext
import org.jetbrains.dukat.js.type.propertyOwner.Scope
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration

private fun ModuleDeclaration.collectConstraints(context: TypeAnalysisContext): Scope {
    val environment = context.getEnvironment()

    val pathWalker = PathWalker()

    calculateConstraints(environment, pathWalker)

    if (pathWalker.startNextPath()) {
        raiseConcern("Conditional at top level not allowed!") {  }
    }

    return environment
}

fun ModuleDeclaration.introduceTypes(context: TypeAnalysisContext) : ModuleDeclaration {
    val environment = collectConstraints(context)

    environment.resolveConstraints()

    val declarations = context.getExportsFrom(environment, resourceName)

    return copy(declarations = declarations)
}

fun SourceFileDeclaration.introduceTypes(context: TypeAnalysisContext) = copy(root = root.introduceTypes(context))

fun SourceSetDeclaration.introduceTypes(context: TypeAnalysisContext = CommonJSContext()) = copy(sources = sources.map { it.introduceTypes(context) })