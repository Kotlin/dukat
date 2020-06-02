package org.jetrbains.dukat.nodeLowering.lowerings

import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.GeneratedInterfaceReferenceNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetrbains.dukat.nodeLowering.NodeTypeLowering


fun ModuleNode.visitTopLevelNode(visitor: (TopLevelEntity) -> Unit) {
    visitor(this)
    declarations.forEach {
        if (it is ModuleNode) {
            it.visitTopLevelNode(visitor)
        } else {
            visitor(it)
        }
    }
}

private class ParameterValueVisitor(private val visit: (paramValue: ParameterValueDeclaration) -> Unit) : NodeTypeLowering {
    override fun lowerType(declaration: TypeNode): TypeNode {
        visit(declaration)
        return super.lowerType(declaration)
    }
}

private fun ModuleNode.removeUnusedReferences(referencesToRemove: Set<String>): ModuleNode {
    val declarationsResolved = declarations.filter {
        !((it is InterfaceNode) && referencesToRemove.contains(it.uid))
    }.map {
        when (it) {
            is ModuleNode -> it.removeUnusedReferences(referencesToRemove)
            else -> it
        }
    }

    return copy(declarations = declarationsResolved)
}

private fun ModuleNode.removeUnusedGeneratedEntities(): ModuleNode {
    val typeRefs = mutableSetOf<String>()
    ParameterValueVisitor { value ->
        when (value) {
            //TODO: we are not supposed to have reference declaration up to this point (but we have)
            is GeneratedInterfaceReferenceNode -> {
                value.reference?.uid?.let { uid ->
                    typeRefs.add(uid)
                }
            }
            is TypeValueNode -> {
                value.typeReference?.uid?.let { uid ->
                    typeRefs.add(uid)
                }
            }
        }
    }.lowerModuleNode(this)


    val unusedGeneratedInterfaces = mutableSetOf<String>()

    visitTopLevelNode { topLevel ->
        if ((topLevel is InterfaceNode) && topLevel.uid.endsWith("_GENERATED")) {
            if (!typeRefs.contains(topLevel.uid)) {
                unusedGeneratedInterfaces.add(topLevel.uid)
            }
        }
    }

    return removeUnusedReferences(unusedGeneratedInterfaces)
}

private fun SourceSetNode.removeUnusedGeneratedEntities() = transform { it.removeUnusedGeneratedEntities() }

class RemoveUnusedGeneratedEntities(): NodeLowering {
    override fun lower(source: SourceSetNode): SourceSetNode {
        return source.removeUnusedGeneratedEntities()
    }
}