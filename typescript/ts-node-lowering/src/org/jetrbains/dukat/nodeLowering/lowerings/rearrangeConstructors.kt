package org.jetrbains.dukat.nodeLowering.lowerings

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.transform

@Suppress("UNCHECKED_CAST")
private fun ModuleNode.rearrangeConstructors(): ModuleNode {
    val loweredDeclarations = declarations.map { declaration ->
        when (declaration) {
            is ClassNode -> {
                val (constructorMembers, members) = declaration.members.partition { it is ConstructorNode }

                var (generatedConstructors, declaredConstructors) = (constructorMembers as List<ConstructorNode>).partition { it.generated }
                generatedConstructors = generatedConstructors.toMutableList()
                declaredConstructors = declaredConstructors.toMutableList()

                val primaryConstructor = when {
                    declaredConstructors.isNotEmpty() -> declaredConstructors.removeAt(0)
                    generatedConstructors.size == 1 -> generatedConstructors.removeAt(0)
                    else -> null
                }

                declaration.copy(
                    members = (declaredConstructors + generatedConstructors + members),
                    primaryConstructor = primaryConstructor
                )
            }
            is ModuleNode -> declaration.rearrangeConstructors()
            else -> declaration
        }
    }

    return copy(declarations = loweredDeclarations)
}


private fun SourceSetNode.rearrangeConstructors() = transform { it.rearrangeConstructors() }

class RearrangeConstructors(): NodeLowering {
    override fun lower(source: SourceSetNode): SourceSetNode {
        return source.rearrangeConstructors()
    }
}