package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.metadata.ThisTypeInGeneratedInterfaceMetaData
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ThisTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetrbains.dukat.nodeLowering.NodeWithOwnerTypeLowering

private class LowerThisType : NodeWithOwnerTypeLowering {

    private fun ClassLikeNode.convertToTypeSignature(): TypeValueNode {

        return when (this) {
            is InterfaceNode -> {
                if (generated) {
                    TypeValueNode(IdentifierEntity("Any"), emptyList(), false, ThisTypeInGeneratedInterfaceMetaData())
                } else {
                    TypeValueNode(IdentifierEntity(name), typeParameters, false, ThisTypeInGeneratedInterfaceMetaData())
                }
            }
            is ClassNode -> TypeValueNode(IdentifierEntity(name), typeParameters, false, ThisTypeInGeneratedInterfaceMetaData())
            else -> TypeValueNode(IdentifierEntity("Any"), emptyList(), false, ThisTypeInGeneratedInterfaceMetaData())
        }
    }

    private fun NodeOwner<*>.classLikeOwnerNode(): ClassLikeNode? {
        val topOwner = generateSequence(this) {
            it.owner
        }.lastOrNull { (it.node is ClassLikeNode) }

        return (topOwner?.node as? ClassLikeNode)
    }

    override fun lowerParameterValue(owner: NodeOwner<ParameterValueDeclaration>): ParameterValueDeclaration {
        val classLikeOwnerNode = owner.classLikeOwnerNode()
        val declaration = owner.node

        return when (declaration) {
            is ThisTypeDeclaration -> if (classLikeOwnerNode == null) {
                super.lowerParameterValue(owner)
            } else {
                classLikeOwnerNode.convertToTypeSignature()
            }
            else -> super.lowerParameterValue(owner)
        }
    }
}

fun DocumentRootNode.lowerThisType(): DocumentRootNode {
    return org.jetbrains.dukat.nodeIntroduction.LowerThisType().lowerRoot(this, NodeOwner(this, null))
}

fun SourceSetNode.lowerThisType() = transform { it.lowerThisType() }