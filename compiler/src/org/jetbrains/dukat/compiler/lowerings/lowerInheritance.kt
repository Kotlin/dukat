package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.AstContext
import org.jetbrains.dukat.ast.model.Declaration
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.duplicate


@Suppress("UNCHECKED_CAST")
fun DocumentRoot.lowerInheritance(astContext: AstContext) : DocumentRoot {
    val declarations = declarations.map {declaration ->
        if (declaration is InterfaceDeclaration) {
            val resolvedParentEntities = declaration.parentEntities
                    .map { parentEntity ->
                        astContext.resolveInterface(parentEntity.name) ?: parentEntity
                    }

            declaration.copy(parentEntities = resolvedParentEntities)
        } else declaration.duplicate<Declaration>()
    }

    return copy(declarations = declarations)
}