package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.declaration.ClassDeclaration
import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.ModifierDeclaration


fun DocumentRootDeclaration.filterOutNonDeclarations(): DocumentRootDeclaration {

    val declarations = declarations.filter { declaration ->
        when (declaration) {
            is ClassDeclaration -> {
                declaration.modifiers.contains(ModifierDeclaration.DECLARE_KEYWORD)
            }
            else -> true
        }
    }

    return copy(declarations = declarations)
}