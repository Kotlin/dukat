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

    val declarations = declarations.map { declaration ->
        when (declaration) {
            is VariableDeclaration -> {
                if (hasExportModifiers(declaration.modifiers)) {
                    listOf(declaration)
                } else emptyList()
            }
            is FunctionDeclaration -> {
                if (hasExportModifiers(declaration.modifiers)) {
                    listOf(declaration)
                } else emptyList()
            }
            is ClassDeclaration -> {
                if (hasExportModifiers(declaration.modifiers)) {
                    listOf(declaration)
                } else emptyList()
            }
            is DocumentRootDeclaration -> {
                listOf(declaration.filterOutNonDeclarations(this))
            }
            else -> listOf(declaration)
        }
    }.flatten()

    return copy(declarations = declarations)
}