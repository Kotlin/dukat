package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode

fun DocumentRootNode.lowerConstructors(): DocumentRootNode {
    val loweredDeclarations = declarations.map { declaration ->
        when (declaration) {
            is ClassNode -> {
                val members = declaration.members.toMutableList()
                val index = members.indexOfFirst { (it is ConstructorNode) && (!it.generated) }
                val primaryConstructor = if (index > -1) members.removeAt(index) else null

                declaration.copy(members = members, primaryConstructor = primaryConstructor as ConstructorNode?)
            }
            is DocumentRootNode -> declaration.lowerConstructors()
            else -> declaration
        }
    }

    return copy(declarations = loweredDeclarations)
}