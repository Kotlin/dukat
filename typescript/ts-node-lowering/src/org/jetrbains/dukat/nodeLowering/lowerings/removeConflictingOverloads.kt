package org.jetrbains.dukat.nodeLowering.lowerings

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.SourceFileNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetrbains.dukat.nodeLowering.NodeTypeLowering

private fun ParameterValueDeclaration.withoutMeta(): ParameterValueDeclaration {
    return when (this) {
        is TypeDeclaration -> copy(meta = null)
        else -> this
    }
}

private fun ParameterNode.withoutMeta(): ParameterNode {
    return copy(type = type.withoutMeta())
}

private fun MemberNode.withoutMeta(): MemberNode {
    return when (this) {
        is MethodNode -> copy(parameters = parameters.map { it.withoutMeta() })
        else -> this
    }
}

private fun filterOutConflictingOverloads(members: List<MemberNode>): List<MemberNode> {
    return members.groupBy { it.withoutMeta() }.map { (_, bucketMembers) ->
        if (bucketMembers.size > 1) {
            bucketMembers.first().withoutMeta()
        } else {
            bucketMembers.first()
        }
    }
}

private class ConflictingOverloads() : NodeTypeLowering {

    override fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode {
        return declaration.copy(members = filterOutConflictingOverloads(declaration.members))
    }

    override fun lowerClassNode(declaration: ClassNode): ClassNode {
        return declaration.copy(members = filterOutConflictingOverloads(declaration.members))
    }
}

fun DocumentRootNode.removeConflictingOverloads(): DocumentRootNode {
    return ConflictingOverloads().lowerDocumentRoot(this)
}

fun SourceFileNode.removeConflictingOverloads() = copy(root = root.removeConflictingOverloads())
fun SourceSetNode.removeConflictingOverloads() =
        copy(sources = sources.map(SourceFileNode::removeConflictingOverloads))
