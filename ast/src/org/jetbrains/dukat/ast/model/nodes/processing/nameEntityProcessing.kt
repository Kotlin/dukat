package org.jetbrains.dukat.ast.model.nodes.processing

import org.jetbrains.dukat.ast.model.nodes.GenericIdentifierNode
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.panic.raiseConcern

val ROOT_PACKAGENAME = IdentifierEntity("<ROOT>")

fun NameEntity.translate(): String = when (this) {
    is IdentifierEntity -> value
    is QualifierEntity -> {
        if (leftMost() == ROOT_PACKAGENAME) {
            shiftLeft()!!.translate()
        } else {
            "${left.translate()}.${right.translate()}"
        }
    }
    is GenericIdentifierNode -> value + "<${typeParameters.joinToString(", ") { typeParameter -> typeParameter.value.translate() }}>"
    else -> raiseConcern("unknown NameEntity ${this}") { this.toString() }
}

fun NameEntity.toNode(): NameEntity {
    return when (this) {
        is IdentifierEntity -> IdentifierEntity(value)
        is QualifierEntity -> QualifierEntity(left = left.toNode(), right = IdentifierEntity(right.value))
        else -> raiseConcern("unknown QualifiedLeftDeclaration") { IdentifierEntity(this.toString()) }
    }
}

private fun NameEntity.countDepth(current: Int): Int {
    return when (this) {
        is IdentifierEntity -> current + 1
        is QualifierEntity -> left.countDepth(current) + right.countDepth(current)
        else -> raiseConcern("unknown org.jetbrains.dukat.astCommon.NameEntity ${this}") { 0 }
    }
}

fun NameEntity.process(handler: (String) -> String): NameEntity {
    return when (this) {
        is IdentifierEntity -> IdentifierEntity(handler(value))
        is QualifierEntity -> copy(left = left.process(handler), right = right.process(handler) as IdentifierEntity)
        is GenericIdentifierNode -> copy(value = handler(value))
        else -> raiseConcern("failed to process NameEntity ${this}") { this }
    }
}

val NameEntity.size: Int
    get() = countDepth(0)

fun String.toNameEntity(): NameEntity {
    return split(".").map { IdentifierEntity(it) }.reduce<NameEntity, IdentifierEntity> { acc, identifier -> identifier.appendRight(acc) }
}

private fun IdentifierEntity.appendLeft(qualifiedLeftNode: NameEntity): QualifierEntity {
    return when (qualifiedLeftNode) {
        is IdentifierEntity -> this.appendLeft(qualifiedLeftNode)
        is QualifierEntity -> this.appendLeft(qualifiedLeftNode)
        else -> raiseConcern("unknown NameEntity ${qualifiedLeftNode}") { QualifierEntity(this, this) }
    }
}

private fun QualifierEntity.appendLeft(qualifiedNode: NameEntity): QualifierEntity {
    return when (left) {
        is IdentifierEntity -> copy(left = (left as IdentifierEntity).appendLeft(qualifiedNode))
        is QualifierEntity -> copy(left = (left as QualifierEntity).appendLeft(qualifiedNode))
        else -> raiseConcern("unknown NameEntity ${left}") { this }
    }
}

fun NameEntity.appendLeft(qualifiedNode: NameEntity): NameEntity {
    return when(this) {
        is IdentifierEntity -> this.appendLeft(qualifiedNode)
        is QualifierEntity -> this.appendLeft(qualifiedNode)
        else -> raiseConcern("unknown NameEntity ${this}") { this }
    }
}

fun IdentifierEntity.appendLeft(identifierNode: IdentifierEntity): QualifierEntity {
    return QualifierEntity(this, identifierNode)
}

fun IdentifierEntity.appendLeft(qualifiedNode: QualifierEntity): QualifierEntity {
    val nodeLeft = qualifiedNode.left
    val left = when (nodeLeft) {
        is IdentifierEntity -> QualifierEntity(this, nodeLeft)
        is QualifierEntity -> {
            val left = nodeLeft.left
            QualifierEntity(when (left) {
                is IdentifierEntity -> this.appendLeft(left)
                is QualifierEntity -> this.appendLeft(left)
                else -> raiseConcern("unkown qualifiedNode $left") { qualifiedNode }
            }, nodeLeft.right)
        }
        else -> raiseConcern("unkown qualifiedNode $nodeLeft") { qualifiedNode }
    }
    return QualifierEntity(left, qualifiedNode.right)
}

fun IdentifierEntity.appendRight(qualifiedLeftNode: NameEntity): QualifierEntity {
    return when (qualifiedLeftNode) {
        is IdentifierEntity -> this.appendRight(qualifiedLeftNode)
        is QualifierEntity -> this.appendRight(qualifiedLeftNode)
        else -> raiseConcern("unknown NameEntity ${qualifiedLeftNode}") { QualifierEntity(qualifiedLeftNode, this) }
    }
}

fun IdentifierEntity.appendRight(qualifiedNode: IdentifierEntity): QualifierEntity {
    return QualifierEntity(qualifiedNode, this)
}

fun IdentifierEntity.appendRight(qualifiedNode: QualifierEntity): QualifierEntity {
    return QualifierEntity(qualifiedNode, this)
}

fun QualifierEntity.appendRight(identifierNode: IdentifierEntity): QualifierEntity {
    return QualifierEntity(this, identifierNode)
}

fun QualifierEntity.appendRight(qualifiedNode: QualifierEntity): QualifierEntity {
    val nodeLeft = qualifiedNode.left
    return when (nodeLeft) {
        is IdentifierEntity -> appendRight(nodeLeft).appendRight(qualifiedNode.right)
        is QualifierEntity -> appendRight(nodeLeft).appendRight(qualifiedNode.right)
        else -> raiseConcern("unknown QualifierEntity") { this }
    }
}

fun NameEntity.appendRight(qualifiedNode: NameEntity): NameEntity {
    return when (this) {
        is IdentifierEntity -> when (qualifiedNode) {
            is IdentifierEntity -> appendRight(qualifiedNode)
            is QualifierEntity -> appendRight(qualifiedNode)
            else -> raiseConcern("unknown QualifierEntity") { this }
        }
        is QualifierEntity -> when (qualifiedNode) {
            is IdentifierEntity -> appendRight(qualifiedNode)
            is QualifierEntity -> appendRight(qualifiedNode)
            else -> raiseConcern("unknown QualifierEntity") { this }
        }
        else -> raiseConcern("unknown QualifierEntity") { this }
    }
}

fun NameEntity.shiftRight(): NameEntity? {
    return when (this) {
        is GenericIdentifierNode -> null
        is IdentifierEntity -> null
        is QualifierEntity -> left
        else -> raiseConcern("unknown NameEntity") { this }
    }
}

fun NameEntity.leftMost(): NameEntity {
    return when (this) {
        is GenericIdentifierNode -> this
        is IdentifierEntity -> this
        is QualifierEntity -> left.leftMost()
        else -> raiseConcern("unknown NameEntity ${this}") { this }
    }
}

fun NameEntity.rightMost(): NameEntity {
    return when (this) {
        is GenericIdentifierNode -> this
        is IdentifierEntity -> this
        is QualifierEntity -> right
        else -> raiseConcern("unknown NameEntity ${this}") { this }
    }
}

fun NameEntity.shiftLeft(): NameEntity? {
    return when (this) {
        is IdentifierEntity -> null
        is QualifierEntity -> {
            val leftShifted = left.shiftLeft()
            if (leftShifted == null) {
                right
            } else {
                QualifierEntity(leftShifted, right)
            }
        }
        else -> raiseConcern("unknown NameEntity") { this }
    }
}

fun QualifierEntity.debugTranslate(): String {
    val nodeLeft = left
    val leftTranslate = when (nodeLeft) {
        is IdentifierEntity -> nodeLeft.value
        is QualifierEntity -> nodeLeft.debugTranslate()
        is GenericIdentifierNode -> nodeLeft.translate()
        else -> raiseConcern("unknown QualifierEntity ${nodeLeft::class.simpleName}") { this.toString() }
    }

    return "${leftTranslate}.${right.value}"
}