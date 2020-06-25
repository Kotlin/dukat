package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.MetaData

data class TypeValueNode(
        val value: NameEntity,
        val params: List<TypeNode>,

        val typeReference: ReferenceNode? = null,
        override var nullable: Boolean = false,
        override var meta: MetaData? = null
) : TypeNode