package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.Declaration


data class HeritageNode(
        val name: HeritageSymbolNode,
        val typeArguments: List<IdentifierNode>
) : Declaration
