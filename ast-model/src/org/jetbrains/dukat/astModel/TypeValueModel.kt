package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.NameNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.astCommon.Declaration

data class TypeValueModel(
        val value: NameNode,
        val params: List<TypeNode>,

        val metaDescription: String?,
        val nullable: Boolean = false
) : Declaration, TypeNode

fun TypeValueModel.isGeneric() = params.isNotEmpty()