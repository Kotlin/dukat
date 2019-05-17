package org.jetbrains.dukat.ast.model.nodes.processing

import org.jetbrains.dukat.ast.model.nodes.GenericIdentifierNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.panic.raiseConcern

fun NameEntity.translate(): String = when (this) {
    is IdentifierEntity -> value
    is QualifiedNode -> {
        "${left.translate()}.${right.translate()}"
    }
    is GenericIdentifierNode -> value + "<${typeParameters.joinToString(", ") { typeParameter -> typeParameter.value.translate() }}>"
    else -> raiseConcern("unknown NameEntity ${this}") { this.toString() }
}

fun NameEntity.toNode(): NameEntity {
    return when (this) {
        is IdentifierEntity -> IdentifierEntity(value)
        is QualifierEntity -> QualifiedNode(left = left.toNode(), right = IdentifierEntity(right.value))
        else -> raiseConcern("unknown QualifiedLeftDeclaration") { IdentifierEntity(this.toString()) }
    }
}

private fun NameEntity.countDepth(current: Int): Int {
    return when (this) {
        is IdentifierEntity -> current + 1
        is QualifiedNode -> left.countDepth(current) + right.countDepth(current)
        else -> raiseConcern("unknown org.jetbrains.dukat.astCommon.NameEntity ${this}") { 0 }
    }
}

fun NameEntity.process(handler: (String) -> String): NameEntity {
    return when (this) {
        is IdentifierEntity -> IdentifierEntity(handler(value))
        is QualifiedNode -> copy(left = left.process(handler), right = right.process(handler) as IdentifierEntity)
        is GenericIdentifierNode -> copy(value = handler(value))
        else -> raiseConcern("failed to process NameEntity ${this}") { this }
    }
}

val NameEntity.size: Int
    get() = countDepth(0)

fun String.toNameEntity(): NameEntity {
    return split(".").map { IdentifierEntity(it) }.reduce<NameEntity, IdentifierEntity> { acc, identifier -> identifier.appendRight(acc) }
}

fun IdentifierEntity.appendLeft(qualifiedLeftNode: NameEntity): QualifiedNode {
    return when (qualifiedLeftNode) {
        is IdentifierEntity -> this.appendLeft(qualifiedLeftNode)
        is QualifiedNode -> this.appendLeft(qualifiedLeftNode)
        else -> raiseConcern("unknown NameEntity ${qualifiedLeftNode}") { QualifiedNode(this, this) }
    }
}

fun IdentifierEntity.appendLeft(identifierNode: IdentifierEntity): QualifiedNode {
    return QualifiedNode(this, identifierNode)
}

fun IdentifierEntity.appendLeft(qualifiedNode: QualifiedNode): QualifiedNode {
    val left = when (qualifiedNode.left) {
        is IdentifierEntity -> QualifiedNode(this, qualifiedNode.left)
        is QualifiedNode -> QualifiedNode(when (qualifiedNode.left.left) {
            is IdentifierEntity -> this.appendLeft(qualifiedNode.left.left)
            is QualifiedNode -> this.appendLeft(qualifiedNode.left.left)
            else -> raiseConcern("unkown qualifiedNode ${qualifiedNode.left.left}") { qualifiedNode }
        }, qualifiedNode.left.right)
        else -> raiseConcern("unkown qualifiedNode ${qualifiedNode.left}") { qualifiedNode }
    }
    return QualifiedNode(left, qualifiedNode.right)
}

fun IdentifierEntity.appendRight(qualifiedLeftNode: NameEntity): QualifiedNode {
    return when (qualifiedLeftNode) {
        is IdentifierEntity -> this.appendRight(qualifiedLeftNode)
        is QualifiedNode -> this.appendRight(qualifiedLeftNode)
        else -> raiseConcern("unknown NameEntity ${qualifiedLeftNode}") { QualifiedNode(qualifiedLeftNode, this) }
    }
}

fun IdentifierEntity.appendRight(qualifiedNode: IdentifierEntity): QualifiedNode {
    return QualifiedNode(qualifiedNode, this)
}

fun IdentifierEntity.appendRight(qualifiedNode: QualifiedNode): QualifiedNode {
    return QualifiedNode(qualifiedNode, this)
}

fun QualifiedNode.appendRight(identifierNode: IdentifierEntity): QualifiedNode {
    return QualifiedNode(this, identifierNode)
}

fun QualifiedNode.appendRight(qualifiedNode: QualifiedNode): QualifiedNode {
    return when (qualifiedNode.left) {
        is IdentifierEntity -> appendRight(qualifiedNode.left).appendRight(qualifiedNode.right)
        is QualifiedNode -> appendRight(qualifiedNode.left).appendRight(qualifiedNode.right)
        else -> raiseConcern("unknown QualifiedNode") { this }
    }
}

fun NameEntity.appendRight(qualifiedNode: NameEntity): NameEntity {
    return when (this) {
        is IdentifierEntity -> when (qualifiedNode) {
            is IdentifierEntity -> appendRight(qualifiedNode)
            is QualifiedNode -> appendRight(qualifiedNode)
            else -> raiseConcern("unknown QualifiedNode") { this }
        }
        is QualifiedNode -> when (qualifiedNode) {
            is IdentifierEntity -> appendRight(qualifiedNode)
            is QualifiedNode -> appendRight(qualifiedNode)
            else -> raiseConcern("unknown QualifiedNode") { this }
        }
        else -> raiseConcern("unknown QualifiedNode") { this }
    }
}

fun NameEntity.shiftRight(): NameEntity? {
    return when (this) {
        is IdentifierEntity -> null
        is GenericIdentifierNode -> null
        is QualifiedNode -> left
        else -> raiseConcern("unknown NameEntity") { this }
    }
}

fun NameEntity.shiftLeft(): NameEntity? {
    return when (this) {
        is IdentifierEntity -> null
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
        is IdentifierEntity -> left.value
        is QualifiedNode -> left.debugTranslate()
        is GenericIdentifierNode -> left.translate()
        else -> raiseConcern("unknown QualifiedNode ${left::class.simpleName}") { this.toString() }
    }

    return "${leftTranslate}.${right.value}"
}