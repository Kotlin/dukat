package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.TopLevelEntity

data class EnumNode(
        val name: String,
        val values: List<EnumTokenNode>
) : TopLevelEntity, TopLevelNode