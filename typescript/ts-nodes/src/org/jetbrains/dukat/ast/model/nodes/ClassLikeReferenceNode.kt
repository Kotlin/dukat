package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.NameEntity

data class ClassLikeReferenceNode(
        val uid: String,
        val name: NameEntity,
        val typeParameters: List<NameEntity>
) : Entity