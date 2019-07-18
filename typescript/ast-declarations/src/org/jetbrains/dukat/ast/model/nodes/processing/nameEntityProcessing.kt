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
