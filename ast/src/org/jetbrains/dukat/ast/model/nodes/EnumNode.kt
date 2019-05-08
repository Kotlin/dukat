package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.AstTopLevelEntity

data class EnumNode(
        val name: String,
        val values: List<EnumTokenNode>
) : AstTopLevelEntity, TopLevelNode