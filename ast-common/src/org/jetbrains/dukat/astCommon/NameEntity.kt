package org.jetbrains.dukat.astCommon

sealed class NameEntity : Entity

data class IdentifierEntity(
        val value: String
) : NameEntity()

data class QualifierEntity(
        val left: NameEntity,
        val right: IdentifierEntity
) : NameEntity()


private operator fun NameEntity.plus(b: NameEntity): NameEntity {
    return  when (b) {
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

fun NameEntity.appendLeft(qualifiedNode: NameEntity): NameEntity {
    return this + qualifiedNode
}

fun NameEntity.appendRight(qualifiedNode: NameEntity): NameEntity {
    return qualifiedNode + this
}

fun NameEntity.shiftRight(): NameEntity? {
    return when (this) {
        is IdentifierEntity -> null
        is QualifierEntity -> left
    }
}

fun NameEntity.leftMost(): NameEntity {
    return when (this) {
        is IdentifierEntity -> this
        is QualifierEntity -> left.leftMost()
    }
}

fun NameEntity.rightMost(): NameEntity {
    return when (this) {
        is IdentifierEntity -> this
        is QualifierEntity -> right
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
    }
}