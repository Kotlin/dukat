package org.jetbrains.dukat.ast.model.nodes.statements

data class ChainCallNode(
        val left: StatementCallNode,
        val right: StatementCallNode
) : StatementNode
