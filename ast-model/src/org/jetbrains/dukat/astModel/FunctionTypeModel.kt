package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class FunctionTypeModel(
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValueDeclaration,

        val metaDescription: String?,
        override var nullable: Boolean = false
) : ParameterValueDeclaration, TypeNode {
    override var meta: ParameterValueDeclaration?
        get() = throw Exception("this exists only for transition period and can not be called")
        set(value) {}
}