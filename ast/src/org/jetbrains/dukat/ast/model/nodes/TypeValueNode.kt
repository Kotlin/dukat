package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class TypeValueNode(
        val value:  NameNode,
        val params: List<ParameterValueDeclaration>,

        override var nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : ParameterValueDeclaration {
    constructor(value: String, params: List<ParameterValueDeclaration>) : this(IdentifierNode(value), params)
    constructor(value: String, params: List<ParameterValueDeclaration>, nullable: Boolean, meta: ParameterValueDeclaration?) : this(IdentifierNode(value), params, nullable, meta)
}

fun TypeValueNode.isPrimitive(primitive: String) : Boolean {
    return when(this.value) {
        is IdentifierNode -> value.value == primitive
        else -> false
    }
}