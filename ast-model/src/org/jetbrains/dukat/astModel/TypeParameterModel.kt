package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.marker.TypeModel
import org.jetbrains.dukat.ast.model.nodes.NameNode
import org.jetbrains.dukat.astCommon.Entity

data class TypeParameterModel(
        val name: NameNode,
        val constraints: List<TypeModel>
) : Entity