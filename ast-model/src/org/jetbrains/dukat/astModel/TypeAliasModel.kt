package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.TopLevelNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode

data class TypeAliasModel(
        val name: String,
        val typeReference: TypeNode,
        val typeParameters: List<TypeParameterModel>
) : TopLevelNode