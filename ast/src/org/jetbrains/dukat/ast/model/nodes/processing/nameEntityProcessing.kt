package org.jetbrains.dukat.ast.model.nodes.processing

import org.jetbrains.dukat.ast.model.nodes.GenericIdentifierNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.QualifierEntity

fun NameEntity.translate(): String = when (this) {
    is IdentifierNode -> value
    is QualifiedNode -> {
        "${left.translate()}.${right.translate()}"
    }
    is GenericIdentifierNode -> value + "<${typeParameters.joinToString(", ") { typeParameter -> typeParameter.value.translate() }}>"
    else -> raiseConcern("unknown NameNode ${this}") { this.toString() }
}

fun NameEntity.toNode(): NameEntity {
    return when (this) {
        is IdentifierEntity -> IdentifierNode(value)
        is QualifierEntity -> QualifiedNode(left = left.toNode(), right = IdentifierNode(right.value))
        else -> raiseConcern("unknown QualifiedLeftDeclaration") { IdentifierNode(this.toString()) }
    }
}

private fun NameEntity.countDepth(current: Int): Int {
    return when (this) {
        is IdentifierNode -> current + 1
        is QualifiedNode -> left.countDepth(current) + right.countDepth(current)
        else -> raiseConcern("unknown org.jetbrains.dukat.astCommon.NameEntity ${this}") { 0 }
    }
}

fun NameEntity.process(handler: (String) -> String): NameEntity {
    return when (this) {
        is IdentifierNode -> IdentifierNode(handler(value))
        is QualifiedNode -> copy(left = left.process(handler), right = right.process(handler) as IdentifierNode)
        is GenericIdentifierNode -> copy(value = handler(value))
        else -> raiseConcern("failed to process NameNode ${this}") { this }
    }
}

val NameEntity.size: Int
    get() = countDepth(0)

fun String.toNameNode(): NameEntity {
    return split(".").map { IdentifierNode(it) }.reduce<NameEntity, IdentifierNode> { acc, identifier -> identifier.appendRight(acc) }
}

fun IdentifierNode.appendLeft(qualifiedLeftNode: NameEntity): QualifiedNode {
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

fun IdentifierNode.appendRight(qualifiedLeftNode: NameEntity): QualifiedNode {
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

fun NameEntity.appendRight(qualifiedNode: NameEntity): NameEntity {
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

fun NameEntity.shiftRight(): NameEntity? {
    return when (this) {
        is IdentifierNode -> null
        is GenericIdentifierNode -> null
        is QualifiedNode -> left
        else -> raiseConcern("unknown NameEntity") { this }
    }
}

fun NameEntity.shiftLeft(): NameEntity? {
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
        else -> raiseConcern("unknown NameEntity") { this }
    }
}

fun QualifiedNode.debugTranslate(): String {
    val leftTranslate = when (left) {
        is IdentifierNode -> left.value
        is QualifiedNode -> left.debugTranslate()
        is GenericIdentifierNode -> left.translate()
        else -> raiseConcern("unknown QualifiedNode ${left::class.simpleName}") { this.toString() }
    }

    return "${leftTranslate}.${right.value}"
}