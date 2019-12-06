package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.metadata.ThisTypeInGeneratedInterfaceMetaData
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.ReferenceEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ThisTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetrbains.dukat.nodeLowering.NodeWithOwnerTypeLowering

private fun processTypeParams(typeParams: List<TypeValueNode>): List<TypeValueNode> {
    return typeParams.map { it.copy(params = emptyList()) }
}

private fun InterfaceNode.convertToTypeSignature(): TypeValueNode? {
    return if (generated) {
        null
    } else {
        TypeValueNode(name, processTypeParams(typeParameters), ReferenceEntity(uid), false, ThisTypeInGeneratedInterfaceMetaData())
    }
}

private fun ClassNode.convertToTypeSignature(): TypeValueNode {
    return TypeValueNode(name, processTypeParams(typeParameters), ReferenceEntity(uid), false, ThisTypeInGeneratedInterfaceMetaData())
}

private fun FunctionNode.convertToTypeSignature(): TypeValueNode {
    val anyNode = TypeValueNode(IdentifierEntity("Any"), emptyList(), null, false, ThisTypeInGeneratedInterfaceMetaData())
    val extendReference = extend
    return if (extendReference != null) {
        TypeValueNode(extendReference.name, emptyList(), ReferenceEntity(uid), false, ThisTypeInGeneratedInterfaceMetaData())
    } else {
        anyNode
    }
}

private fun NodeOwner<*>.classLikeOwnerNode(): TopLevelEntity? {
    val topOwner = generateSequence(this) {
        it.owner
    }.lastOrNull { (it.node is ClassLikeNode) || (it.node is FunctionNode) }

    return (topOwner?.node as? TopLevelEntity)
}

private class LowerThisType : NodeWithOwnerTypeLowering {

    override fun lowerParameterValue(owner: NodeOwner<ParameterValueDeclaration>): ParameterValueDeclaration {
        val declaration = owner.node

        return when (declaration) {
            is ThisTypeDeclaration -> {
                val contextNode = owner.classLikeOwnerNode();
                val anyNode = TypeValueNode(IdentifierEntity("Any"), emptyList(), null, false, ThisTypeInGeneratedInterfaceMetaData())

                when (contextNode) {
                    is ClassNode -> contextNode.convertToTypeSignature()
                    is InterfaceNode -> contextNode.convertToTypeSignature() ?: anyNode
                    is FunctionNode -> contextNode.convertToTypeSignature()
                    else -> anyNode
                }
            }
            else -> super.lowerParameterValue(owner)
        }
    }
}

fun DocumentRootNode.lowerThisType(): DocumentRootNode {
    return LowerThisType().lowerRoot(this, NodeOwner(this, null))
}

fun SourceSetNode.lowerThisType() = transform { it.lowerThisType() }