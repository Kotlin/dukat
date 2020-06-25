package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.MetaData

data class TupleTypeNode(
        val params: List<TypeNode>,

        override var nullable: Boolean = false,
        override var meta: MetaData? = null
) : TypeNode