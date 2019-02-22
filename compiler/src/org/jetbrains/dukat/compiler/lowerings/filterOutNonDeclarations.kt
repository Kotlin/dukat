package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.PackageDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.converters.SourceFileDeclaration


private fun hasExportModifiers(modifiers: List<ModifierDeclaration>): Boolean {
    return modifiers.contains(ModifierDeclaration.EXPORT_KEYWORD)
            || modifiers.contains(ModifierDeclaration.DECLARE_KEYWORD)
}

fun PackageDeclaration.filterOutNonDeclarations(isSubModule: Boolean): PackageDeclaration {

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
            is PackageDeclaration -> {
                if (isSubModule || hasExportModifiers(declaration.modifiers)) {
                    listOf(declaration.filterOutNonDeclarations(true))
                } else emptyList()
            }
            else -> listOf(declaration)
        }
    }.flatten()

    return copy(declarations = declarations)
}

fun SourceFileDeclaration.filterOutNonDeclarations() = copy(root = root.filterOutNonDeclarations(false))