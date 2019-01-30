package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode

fun DocumentRootDeclaration.lowerConstructors(): DocumentRootDeclaration {
    val loweredDeclarations = declarations.map { declaration ->
        when (declaration) {
            is ClassNode -> {
                val members = declaration.members.toMutableList()
                val index = members.indexOfFirst { (it is ConstructorNode) && (!it.generated) }
                val primaryConstructor = if (index > -1) members.removeAt(index) else null

                declaration.copy(members = members, primaryConstructor = primaryConstructor as ConstructorNode?)
            }
            else -> declaration
        }
    }

    return copy(declarations = loweredDeclarations)
}