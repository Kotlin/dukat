package org.jetbrains.dukat.js.type.analysis

import org.jetbrains.dukat.js.type.exportResolution.CommonJSContext
import org.jetbrains.dukat.js.type.exportResolution.TypeAnalysisContext
import org.jetbrains.dukat.js.type.propertyOwner.Scope
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.WithModifiersDeclaration

private fun ModuleDeclaration.collectConstraints(context: TypeAnalysisContext): Scope {
    val environment = context.getEnvironment()

    val pathWalker = PathWalker()

    calculateConstraints(environment, pathWalker)

    if (pathWalker.startNextPath()) {
        raiseConcern("Conditional at top level not allowed!") { }
    }

    return environment
}

private fun WithModifiersDeclaration.makeExternal(): Set<ModifierDeclaration> {
    return if (!hasDeclareModifier()) {
        modifiers + ModifierDeclaration.DECLARE_KEYWORD
    } else {
        modifiers
    }
}

fun ModuleDeclaration.introduceTypes(context: TypeAnalysisContext): ModuleDeclaration {
    val environment = collectConstraints(context)

    environment.resolveConstraints()

    val declarations =
            context.getExportsFrom(environment, resourceName)
                    .map {
                        when (it) {
                            is FunctionDeclaration -> it.copy(modifiers = makeExternal())
                            is VariableDeclaration -> it.copy(modifiers = makeExternal())
                            is ClassDeclaration -> it.copy(modifiers = makeExternal())
                            else -> it
                        }
                    }

    return copy(declarations = declarations)
}

fun SourceFileDeclaration.introduceTypes(context: TypeAnalysisContext) = copy(root = root.introduceTypes(context))

fun SourceSetDeclaration.introduceTypes(context: TypeAnalysisContext = CommonJSContext()) = copy(sources = sources.map { it.introduceTypes(context) })