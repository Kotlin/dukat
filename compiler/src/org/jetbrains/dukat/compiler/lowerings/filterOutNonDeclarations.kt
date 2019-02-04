package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration


private fun hasExportModifiers(modifiers: List<ModifierDeclaration>): Boolean {
    return modifiers.contains(ModifierDeclaration.EXPORT_KEYWORD)
            || modifiers.contains(ModifierDeclaration.DECLARE_KEYWORD)
}

fun DocumentRootDeclaration.filterOutNonDeclarations(parent: DocumentRootDeclaration? = null): DocumentRootDeclaration {

    val isSubModule = parent != null

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
            is DocumentRootDeclaration -> {
                if (isSubModule || hasExportModifiers(declaration.modifiers)) {
                    listOf(declaration.filterOutNonDeclarations(this))
                } else emptyList()
            }
            else -> listOf(declaration)
        }
    }.flatten()

    return copy(declarations = declarations)
}