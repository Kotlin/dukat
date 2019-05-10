package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.AstEntity
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class ParameterNode(
        val name: String,
        val type: ParameterValueDeclaration,
        val initializer: TypeValueNode?,
        val meta: String?,

        val vararg: Boolean,
        val optional: Boolean
) : AstEntity


fun ParameterDeclaration.convertToNode(): ParameterNode = ParameterNode(
    name = name,
    type = type,
    initializer = if (initializer != null) {
        TypeValueNode(IdentifierNode(initializer!!.kind.value.value), emptyList())
    } else null,
    meta = initializer?.meta,
    vararg = vararg,
    optional = optional
)