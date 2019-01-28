package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.AstContext
import org.jetbrains.dukat.ast.model.declaration.ClassDeclaration
import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TopLevelDeclaration
import org.jetbrains.dukat.ast.model.duplicate


@Suppress("UNCHECKED_CAST")
fun DocumentRootDeclaration.lowerInheritance(astContext: AstContext): DocumentRootDeclaration {
    val declarations = declarations.map { declaration ->
        if (declaration is InterfaceDeclaration) {
            val resolvedParentEntities = declaration.parentEntities
                    .map { parentEntity ->
                        astContext.resolveInterface(parentEntity.name) ?: parentEntity
                    }

            declaration.copy(parentEntities = resolvedParentEntities)
        } else if (declaration is ClassDeclaration) {
            val resolvedParentEntities = declaration.parentEntities
                    .map { parentEntity ->
                        val parentEntityResolved = when (parentEntity) {
                            is InterfaceDeclaration -> astContext.resolveInterface(parentEntity.name)
                            is ClassDeclaration -> astContext.resolveClass(parentEntity.name)
                            else -> parentEntity
                        }

                         parentEntityResolved ?: parentEntity
                    }

            declaration.copy(parentEntities = resolvedParentEntities)
        } else if (declaration is DocumentRootDeclaration) {
            declaration.lowerInheritance(astContext)
        }
        else declaration.duplicate<TopLevelDeclaration>()
    }

    return copy(declarations = declarations)
}