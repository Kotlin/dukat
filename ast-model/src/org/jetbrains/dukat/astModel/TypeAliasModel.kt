package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.TopLevelNode
import org.jetbrains.dukat.astCommon.NameEntity

data class TypeAliasModel(
        override val name: NameEntity,
        val typeReference: TypeModel,
        val typeParameters: List<TypeParameterModel>
) : TopLevelNode