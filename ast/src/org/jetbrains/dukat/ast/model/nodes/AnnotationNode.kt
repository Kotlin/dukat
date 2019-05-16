package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.NameEntity

data class AnnotationNode(
        val name: String,
        val params: List<NameEntity>
) : Entity