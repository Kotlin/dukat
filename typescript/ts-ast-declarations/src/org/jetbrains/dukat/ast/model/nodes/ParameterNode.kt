package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnknownExpressionDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class ParameterNode(
        val name: String,
        val type: ParameterValueDeclaration,
        val initializer: TypeValueNode?,
        val meta: String?,

        val vararg: Boolean,
        val optional: Boolean
) : Entity


fun ParameterDeclaration.convertToNode(): ParameterNode = ParameterNode(
        name = name,
        type = type,
        initializer = if (initializer != null || optional) {
            TypeValueNode(IdentifierEntity("definedExternally"), emptyList())
        } else null,
        meta = if (initializer != null && initializer is UnknownExpressionDeclaration) {
            (initializer as UnknownExpressionDeclaration).meta
        } else null,
        vararg = vararg,
        optional = optional
)