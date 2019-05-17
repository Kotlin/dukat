package org.jetbrains.dukat.ast.model.nodes

data class ChainCallNode(
    val left: StatementCallNode,
    val right:  StatementCallNode
) : StatementNode
