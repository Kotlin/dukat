package org.jetrbains.dukat.nodeLowering.lowerings

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.transform

@Suppress("UNCHECKED_CAST")
fun DocumentRootNode.rearrangeConstructors(): DocumentRootNode {
    val loweredDeclarations = declarations.map { declaration ->
        when (declaration) {
            is ClassNode -> {
                val (constructorMembers, members) = declaration.members.partition { it is ConstructorNode }

                var (generatedConstructors, declaredConstructors) = (constructorMembers as List<ConstructorNode>).partition { it.generated }
                generatedConstructors = generatedConstructors.toMutableList()
                declaredConstructors = declaredConstructors.toMutableList()

                val primaryConstructor = if (declaredConstructors.isNotEmpty()) {
                    declaredConstructors.removeAt(0)
                } else if (generatedConstructors.size == 1) {
                    generatedConstructors.removeAt(0)
                } else null

                declaration.copy(
                    members = (declaredConstructors + generatedConstructors + members),
                    primaryConstructor = primaryConstructor
                )
            }
            is DocumentRootNode -> declaration.rearrangeConstructors()
            else -> declaration
        }
    }

    return copy(declarations = loweredDeclarations)
}


fun SourceSetNode.rearrangeConstructors() = transform { it.rearrangeConstructors() }