package org.jetrbains.dukat.nodeLowering.lowerings

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.GeneratedInterfaceReferenceNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetrbains.dukat.nodeLowering.NodeWithOwnerTypeLowering


fun DocumentRootNode.visitTopLevelNode(visitor: (TopLevelEntity) -> Unit) {
    visitor(this)
    declarations.forEach {
        if (it is DocumentRootNode) {
            it.visitTopLevelNode(visitor)
        } else {
            visitor(it)
        }
    }
}

private class ParameterValueVisitor(private val documentRoot: DocumentRootNode, private val visit: (paramValue: ParameterValueDeclaration) -> Unit) : NodeWithOwnerTypeLowering {

    override fun lowerParameterValue(owner: NodeOwner<ParameterValueDeclaration>): ParameterValueDeclaration {
        visit(owner.node)
        return super.lowerParameterValue(owner)
    }

    fun visit() {
        lowerRoot(documentRoot, NodeOwner(documentRoot, null))
    }
}

private fun DocumentRootNode.removeUnusedReferences(referencesToRemove: Set<String>): DocumentRootNode {
    val declarationsResolved = declarations.filter {
        !((it is InterfaceNode) && referencesToRemove.contains(it.uid))
    }.map {
        when (it) {
            is DocumentRootNode -> it.removeUnusedReferences(referencesToRemove)
            else -> it
        }
    }

    return copy(declarations = declarationsResolved)
}

fun DocumentRootNode.removeUnusedGeneratedEntities(): DocumentRootNode {
    val typeRefs = mutableSetOf<String>()
    ParameterValueVisitor(this) { value ->
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
    }.visit()


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

fun SourceSetNode.removeUnusedGeneratedEntities() = transform { it.removeUnusedGeneratedEntities() }