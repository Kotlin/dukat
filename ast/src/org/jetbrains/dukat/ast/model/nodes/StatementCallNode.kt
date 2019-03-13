package org.jetbrains.dukat.ast.model.nodes

data class StatementCallNode(
    val value: String
) : StatementNode, QualifiedStatementLeftNode, QualifiedStatementRightNode
