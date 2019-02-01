package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration


fun DocumentRootDeclaration.filterOutNonDeclarations(): DocumentRootDeclaration {

    val declarations = declarations.filter { declaration ->
        when (declaration) {
            is ClassDeclaration -> {
                declaration.modifiers.contains(ModifierDeclaration.EXPORT_KEYWORD) ||
                        declaration.modifiers.contains(ModifierDeclaration.DECLARE_KEYWORD)
            }
            else -> true
        }
    }

    return copy(declarations = declarations)
}