package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.*
import org.jetbrains.dukat.ast.model.nodes.constants.SELF_REFERENCE_TYPE
import org.jetbrains.dukat.ast.model.nodes.metadata.ThisTypeInGeneratedInterfaceMetaData
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetrbains.dukat.nodeLowering.NodeWithOwnerTypeLowering
import org.jetrbains.dukat.nodeLowering.lowerings.NodeLowering

private fun processTypeParams(typeParams: List<TypeValueNode>): List<TypeValueNode> {
    return typeParams.map { it.copy(params = emptyList()) }
}

private fun InterfaceNode.convertToTypeSignature(): TypeValueNode? {
    return if (generated) {
        null
    } else {
        TypeValueNode(name, processTypeParams(typeParameters), ReferenceNode(uid), false, ThisTypeInGeneratedInterfaceMetaData())
    }
}

private fun ClassNode.convertToTypeSignature(): TypeValueNode {
    return TypeValueNode(name, processTypeParams(typeParameters), ReferenceNode(uid), false, ThisTypeInGeneratedInterfaceMetaData())
}

private fun FunctionNode.convertToTypeSignature(): TypeValueNode {
    val extendReference = extend
    return if (extendReference != null) {
        // TODO: ideally  it would be nice to have real TypeValueNodes in class reference (or got rid of class references completely
        val typeParams = extendReference.typeParameters.map { TypeValueNode(it, emptyList(), null) }
        TypeValueNode(extendReference.name, typeParams, ReferenceNode(uid), false, ThisTypeInGeneratedInterfaceMetaData())
    } else {
        SELF_REFERENCE_TYPE.copy()
    }
}

private fun NodeOwner<*>.classLikeOwnerNode(): TopLevelEntity? {
    val topOwner = generateSequence(this) {
        it.owner
    }.lastOrNull { (it.node is ClassLikeNode) || (it.node is FunctionNode) }

    return (topOwner?.node as? TopLevelEntity)
}

private class LowerThisTypeNodeLowering : NodeWithOwnerTypeLowering {

    override fun lowerType(owner: NodeOwner<TypeNode>): TypeNode {
        val declaration = owner.node

        return when (declaration) {
            SELF_REFERENCE_TYPE -> {
                val contextNode = owner.classLikeOwnerNode()
                val anyNode = SELF_REFERENCE_TYPE.copy()

                when (contextNode) {
                    is ClassNode -> contextNode.convertToTypeSignature()
                    is InterfaceNode -> contextNode.convertToTypeSignature() ?: anyNode
                    is FunctionNode -> contextNode.convertToTypeSignature()
                    else -> anyNode
                }
            }
            else -> super.lowerType(owner)
        }
    }
}

private fun ModuleNode.lowerThisType(): ModuleNode {
    return LowerThisTypeNodeLowering().lowerRoot(this, NodeOwner(this, null))
}

private fun SourceSetNode.lowerThisType() = transform { it.lowerThisType() }

class LowerThisType():NodeLowering {
    override fun lower(source: SourceSetNode): SourceSetNode {
        return source.lowerThisType()
    }
}