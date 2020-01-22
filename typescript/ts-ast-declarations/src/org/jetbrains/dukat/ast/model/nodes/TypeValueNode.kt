package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.ReferenceEntity
import org.jetbrains.dukat.astCommon.TypeEntity
import org.jetbrains.dukat.tsmodel.Declaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class TypeValueNode(
        val value: NameEntity,
        val params: List<ParameterValueDeclaration>,

        val typeReference: ReferenceNode? = null,
        override var nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : TypeNode


private val UNIT = TypeValueNode(IdentifierEntity("Unit"), emptyList(), null, false, null)

fun TypeEntity.isUnit(): Boolean {
    return (this is TypeValueNode) && isUnit()
}

fun TypeValueNode.isUnit(): Boolean {
    return copy(typeReference = null) == UNIT
}

fun TypeValueNode.isPrimitive(primitive: String): Boolean {
    return when (this.value) {
        is IdentifierEntity -> value.value == primitive
        else -> false
    }
}