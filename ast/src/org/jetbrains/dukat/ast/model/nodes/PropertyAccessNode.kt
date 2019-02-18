package org.jetbrains.dukat.ast.model.nodes

data class PropertyAccessNode(
        val name: IdentifierNode,
        val expression: HeritageSymbolNode
) : HeritageSymbolNode
