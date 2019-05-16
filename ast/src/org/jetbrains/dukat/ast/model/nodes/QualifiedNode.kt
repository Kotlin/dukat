package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ModuleReferenceDeclaration

data class QualifiedNode(
        val left: NameNode,
        val right: IdentifierNode
) : ModuleReferenceDeclaration, NameNode


fun IdentifierNode.appendLeft(qualifiedLeftNode: NameNode): QualifiedNode {
    return when (qualifiedLeftNode) {
        is IdentifierNode -> this.appendLeft(qualifiedLeftNode)
        is QualifiedNode -> this.appendLeft(qualifiedLeftNode)
        else -> raiseConcern("unknown NameNode ${qualifiedLeftNode}") { QualifiedNode(this, this) }
    }
}

fun IdentifierNode.appendLeft(identifierNode: IdentifierNode): QualifiedNode {
    return QualifiedNode(this, identifierNode)
}

fun IdentifierNode.appendLeft(qualifiedNode: QualifiedNode): QualifiedNode {
    val left = when (qualifiedNode.left) {
        is IdentifierNode -> QualifiedNode(this, qualifiedNode.left)
        is QualifiedNode -> QualifiedNode(when (qualifiedNode.left.left) {
            is IdentifierNode -> this.appendLeft(qualifiedNode.left.left)
            is QualifiedNode -> this.appendLeft(qualifiedNode.left.left)
            else -> raiseConcern("unkown qualifiedNode ${qualifiedNode.left.left}") { qualifiedNode }
        }, qualifiedNode.left.right)
        else -> raiseConcern("unkown qualifiedNode ${qualifiedNode.left}") { qualifiedNode }
    }
    return QualifiedNode(left, qualifiedNode.right)
}

fun IdentifierNode.appendRight(qualifiedLeftNode: NameNode): QualifiedNode {
    return when (qualifiedLeftNode) {
        is IdentifierNode -> this.appendRight(qualifiedLeftNode)
        is QualifiedNode -> this.appendRight(qualifiedLeftNode)
        else -> raiseConcern("unknown NameNode ${qualifiedLeftNode}") { QualifiedNode(qualifiedLeftNode, this) }
    }
}

fun IdentifierNode.appendRight(qualifiedNode: IdentifierNode): QualifiedNode {
    return QualifiedNode(qualifiedNode, this)
}

fun IdentifierNode.appendRight(qualifiedNode: QualifiedNode): QualifiedNode {
    return QualifiedNode(qualifiedNode, this)
}

fun QualifiedNode.appendRight(identifierNode: IdentifierNode): QualifiedNode {
    return QualifiedNode(this, identifierNode)
}

fun QualifiedNode.appendRight(qualifiedNode: QualifiedNode): QualifiedNode {
    return when (qualifiedNode.left) {
        is IdentifierNode -> appendRight(qualifiedNode.left).appendRight(qualifiedNode.right)
        is QualifiedNode -> appendRight(qualifiedNode.left).appendRight(qualifiedNode.right)
        else -> raiseConcern("unknown QualifiedNode") { this }
    }
}

fun NameNode.appendRight(qualifiedNode: NameNode): NameNode {
    return when (this) {
        is IdentifierNode -> when (qualifiedNode) {
            is IdentifierNode -> appendRight(qualifiedNode)
            is QualifiedNode -> appendRight(qualifiedNode)
            else -> raiseConcern("unknown QualifiedNode") { this }
        }
        is QualifiedNode -> when (qualifiedNode) {
            is IdentifierNode -> appendRight(qualifiedNode)
            is QualifiedNode -> appendRight(qualifiedNode)
            else -> raiseConcern("unknown QualifiedNode") { this }
        }
        else -> raiseConcern("unknown QualifiedNode") { this }
    }
}


fun NameNode.shiftRight(): NameNode? {
    return when (this) {
        is IdentifierNode -> null
        is GenericIdentifierNode -> null
        is QualifiedNode -> left
        else -> raiseConcern("unknown NameNode") { this }
    }
}

fun NameNode.shiftLeft(): NameNode? {
    return when (this) {
        is IdentifierNode -> null
        is QualifiedNode -> {
            val leftShifted = left.shiftLeft()
            if (leftShifted == null) {
                right
            } else {
                QualifiedNode(leftShifted, right)
            }
        }
        else -> raiseConcern("unknown NameNode") { this }
    }
}


fun IdentifierNode.debugTranslate(): String = value
fun QualifiedNode.debugTranslate(): String {
    val leftTranslate = when (left) {
        is IdentifierNode -> left.value
        is QualifiedNode -> left.debugTranslate()
        is GenericIdentifierNode -> left.translate()
        else -> raiseConcern("unknown QualifiedNode ${left::class.simpleName}") { this.toString() }
    }

    return "${leftTranslate}.${right.value}"
}