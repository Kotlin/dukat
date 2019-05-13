package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.marker.TypeModel
import org.jetbrains.dukat.ast.model.nodes.NameNode
import org.jetbrains.dukat.ast.model.nodes.TopLevelNode

data class TypeAliasModel(
        val name: NameNode,
        val typeReference: TypeModel,
        val typeParameters: List<TypeParameterModel>
) : TopLevelNode