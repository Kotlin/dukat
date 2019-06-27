package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity

data class EnumNode(
        val name: NameEntity,
        val values: List<EnumTokenNode>
) : TopLevelEntity