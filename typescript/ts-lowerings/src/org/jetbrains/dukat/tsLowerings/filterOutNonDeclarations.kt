package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionStatementDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration


private fun hasExportModifiers(modifiers: List<ModifierDeclaration>): Boolean {
    return modifiers.contains(ModifierDeclaration.EXPORT_KEYWORD)
            || modifiers.contains(ModifierDeclaration.DECLARE_KEYWORD)
}

fun ModuleDeclaration.filterOutNonDeclarations(isSubModule: Boolean): ModuleDeclaration {

    val declarations = declarations.map { declaration ->
        when (declaration) {
            is VariableDeclaration -> {
                if (isSubModule || hasExportModifiers(declaration.modifiers)) {
                    listOf(declaration)
                } else emptyList()
            }
            is FunctionDeclaration -> {
                if (isSubModule || hasExportModifiers(declaration.modifiers)) {
                    listOf(declaration)
                } else emptyList()
            }
            is ClassDeclaration -> {
                if (isSubModule || hasExportModifiers(declaration.modifiers)) {
                    listOf(declaration)
                } else emptyList()
            }
            is ModuleDeclaration -> {
                if (isSubModule || hasExportModifiers(declaration.modifiers)) {
                    listOf(declaration.filterOutNonDeclarations(true))
                } else emptyList()
            }
            is ExpressionStatementDeclaration -> emptyList()
            else -> listOf(declaration)
        }
    }.flatten()

    return copy(declarations = declarations)
}

fun SourceFileDeclaration.filterOutNonDeclarations(): SourceFileDeclaration {
   return copy(root = root.filterOutNonDeclarations(false))
}

private fun SourceSetDeclaration.filterOutNonDeclarations() = copy(sources = sources.map(SourceFileDeclaration::filterOutNonDeclarations))

class FilterOutNonDeclarations : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.filterOutNonDeclarations()
    }
}