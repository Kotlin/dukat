package org.jetbrains.dukat.astCommon

sealed class NameEntity : Entity

data class IdentifierEntity(
        val value: String
) : NameEntity() {
    override fun toString() = value
}

data class QualifierEntity(
        val left: NameEntity,
        val right: IdentifierEntity
) : NameEntity() {
    override fun toString() = "${left}.${right}"
}

private operator fun NameEntity.plus(b: NameEntity): QualifierEntity {
    return when (b) {
        is IdentifierEntity -> QualifierEntity(this, b)
        is QualifierEntity -> ((this + b.left) + b.right)
    }
}

fun NameEntity.process(handler: (String) -> String): NameEntity {
    return when (this) {
        is IdentifierEntity -> IdentifierEntity(handler(value))
        is QualifierEntity -> copy(left = left.process(handler), right = right.process(handler) as IdentifierEntity)
    }
}

fun NameEntity.appendLeft(qualifiedNode: NameEntity): QualifierEntity {
    return this + qualifiedNode
}

fun NameEntity.appendRight(qualifiedNode: NameEntity): QualifierEntity {
    return qualifiedNode + this
}

fun NameEntity.shiftRight(): NameEntity? {
    return when (this) {
        is IdentifierEntity -> null
        is QualifierEntity -> left
    }
}

fun NameEntity.leftMost(): IdentifierEntity {
    return when (this) {
        is IdentifierEntity -> this
        is QualifierEntity -> left.leftMost()
    }
}

fun NameEntity.rightMost(): IdentifierEntity {
    return when (this) {
        is IdentifierEntity -> this
        is QualifierEntity -> right.rightMost()
    }
}

fun NameEntity.shiftLeft(): NameEntity? {
    return when (this) {
        is IdentifierEntity -> null
        is QualifierEntity -> shiftLeft()
    }
}

fun QualifierEntity.shiftLeft(): NameEntity {
    val leftShifted = left.shiftLeft()
    return if (leftShifted == null) {
        right
    } else {
        QualifierEntity(leftShifted, right)
    }
}

fun String.toNameEntity(): NameEntity {
    return split(".").map { IdentifierEntity(it) }.reduce<NameEntity, IdentifierEntity> { acc, identifier -> identifier.appendRight(acc) }
}

fun NameEntity.hasPrefix(vararg prefixes: IdentifierEntity): Boolean {
    val left = leftMost()
    return prefixes.any { left == it }
}
