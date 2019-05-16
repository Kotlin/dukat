package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class TypeValueNode(
        val value: NameEntity,
        val params: List<ParameterValueDeclaration>,

        override var nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : TypeNode

fun TypeValueNode.isPrimitive(primitive: String): Boolean {
    return when (this.value) {
        is IdentifierNode -> value.value == primitive
        else -> false
    }
}