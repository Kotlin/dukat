package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.marker.TypeModel
import org.jetbrains.dukat.ast.model.nodes.NameNode
import org.jetbrains.dukat.astCommon.Entity

data class TypeValueModel(
        val value: NameNode,
        val params: List<TypeModel>,

        val metaDescription: String?,
        val nullable: Boolean = false
) : Entity, TypeModel

fun TypeValueModel.isGeneric() = params.isNotEmpty()