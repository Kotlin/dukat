package org.jetbrains.dukat.ast.model.nodes.processing

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.appendRight

fun NameEntity.toNode(): NameEntity {
    return when (this) {
        is IdentifierEntity -> IdentifierEntity(value)
        is QualifierEntity -> QualifierEntity(left = left.toNode(), right = IdentifierEntity(right.value))
    }
}

private fun NameEntity.countDepth(current: Int): Int {
    return when (this) {
        is IdentifierEntity -> current + 1
        is QualifierEntity -> left.countDepth(current) + right.countDepth(current)
    }
}

val NameEntity.size: Int
    get() = countDepth(0)

fun String.toNameEntity(): NameEntity {
    return split(".").map { IdentifierEntity(it) }.reduce<NameEntity, IdentifierEntity> { acc, identifier -> identifier.appendRight(acc) }
}

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

private fun unquote(name: String): String {
    return name.replace("(?:^[\"\'])|(?:[\"\']$)".toRegex(), "")
}

fun NameEntity.unquote(): NameEntity {
    return when (this) {
        is IdentifierEntity -> copy(value = escapeName(unquote(value)))
        else -> this
    }
}
