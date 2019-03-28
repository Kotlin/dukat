package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.astCommon.Declaration

data class TypeParameterModel(
        val name: String,
        val constraints: List<TypeNode>
) : Declaration