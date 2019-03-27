package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.NameNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.astCommon.Declaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class TypeValueModel(
        val value: NameNode,
        val params: List<ParameterValueDeclaration>,

        val metaDescription: String?,
        override val nullable: Boolean = false
) : Declaration, ParameterValueDeclaration, TypeNode {
    override var meta: ParameterValueDeclaration?
        get() = throw Exception("this exists only for transition period and can not be called")
        set(value) {}
}


fun TypeValueModel.isGeneric() = params.isNotEmpty()