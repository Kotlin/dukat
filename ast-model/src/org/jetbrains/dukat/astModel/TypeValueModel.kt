package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.NameNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.astCommon.AstEntity

data class TypeValueModel(
        val value: NameNode,
        val params: List<TypeNode>,

        val metaDescription: String?,
        val nullable: Boolean = false
) : AstEntity, TypeNode

fun TypeValueModel.isGeneric() = params.isNotEmpty()