package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.TopLevelDeclaration

data class EnumNode(
        val name: String,
        val values: List<EnumTokenNode>
) : TopLevelDeclaration, TopLevelNode