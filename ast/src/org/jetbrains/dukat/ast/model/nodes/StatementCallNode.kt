package org.jetbrains.dukat.ast.model.nodes

data class StatementCallNode(
    val value: String,
    val params: List<IdentifierNode>
) : StatementNode, QualifiedStatementLeftNode, QualifiedStatementRightNode
