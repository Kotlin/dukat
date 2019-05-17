package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.metadata.ThisTypeInGeneratedInterfaceMetaData
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.tsmodel.ThisTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private class LowerThisType {

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

    private fun ParameterValueDeclaration.lower(owner: ClassLikeNode): ParameterValueDeclaration {
        return when (this) {
            is ThisTypeDeclaration -> owner.convertToTypeSignature()
            else -> this
        }
    }

    fun lowerMemberNode(member: MemberNode, owner: ClassLikeNode): MemberNode {
        return when (member) {
            is PropertyNode -> member.copy(type = member.type.lower(owner))
            is MethodNode -> member.copy(type = member.type.lower(owner))
            else -> member
        }
    }

    fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode {
        return declaration.copy(members = declaration.members.map { lowerMemberNode(it, declaration) })
    }

    fun lowerClassNode(declaration: ClassNode): ClassNode {
        return declaration.copy(members = declaration.members.map { lowerMemberNode(it, declaration) })
    }

    fun lowerTopLevelDeclaration(declaration: TopLevelEntity): TopLevelEntity {
        return when (declaration) {
            is InterfaceNode -> lowerInterfaceNode(declaration)
            is ClassNode -> lowerClassNode(declaration)
            else -> declaration
        }
    }

    fun lowerDocumentRoot(documentRootNode: DocumentRootNode): DocumentRootNode {
        return documentRootNode.copy(declarations = documentRootNode.declarations.map { lowerTopLevelDeclaration(it) })
    }
}

fun DocumentRootNode.lowerThisType(): DocumentRootNode {
    return org.jetbrains.dukat.nodeIntroduction.LowerThisType().lowerDocumentRoot(this)
}

fun SourceSetNode.lowerThisType() = transform { it.lowerThisType() }