package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.AstEntity


data class HeritageNode(
        val name: HeritageSymbolNode,
        val typeArguments: List<NameNode>
) : AstEntity
