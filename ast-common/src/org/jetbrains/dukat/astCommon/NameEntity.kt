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

fun NameEntity.rightMost(): IdentifierEntity {
    return when (this) {
        is IdentifierEntity -> this
        is QualifierEntity -> right.rightMost()
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

fun String.toNameEntity(): NameEntity {
    return split(".").map { IdentifierEntity(it) }.reduce<NameEntity, IdentifierEntity> { acc, identifier -> identifier.appendRight(acc) }
}


private fun NameEntity.countDepth(current: Int): Int {
    return when (this) {
        is IdentifierEntity -> current + 1
        is QualifierEntity -> left.countDepth(current) + right.countDepth(current)
    }
}

val NameEntity.size: Int
    get() = countDepth(0)

//TODO: this should be done somewhere near escapeIdentificators (at least code should be reused)
private fun escapeName(name: String): String {
    return name
            .replace("/".toRegex(), ".")
            .replace("-".toRegex(), "_")
            .replace("^_$".toRegex(), "`_`")
            .replace("^class$".toRegex(), "`class`")
            .replace("^var$".toRegex(), "`var`")
            .replace("^val$".toRegex(), "`val`")
            .replace("^interface$".toRegex(), "`interface`")
}

//TODO: it actually hardly belongs here
private fun String.unquote(): String {
    return replace("(?:^[\"\'])|(?:[\"\']$)".toRegex(), "")
}

fun NameEntity.unquote(): NameEntity {
    return when (this) {
        is IdentifierEntity -> copy(value = escapeName(value.unquote()))
        else -> this
    }
}