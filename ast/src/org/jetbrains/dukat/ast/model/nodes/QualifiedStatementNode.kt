package org.jetbrains.dukat.ast.model.nodes

data class QualifiedStatementNode(
    val left: QualifiedStatementLeftNode,
    val right:  QualifiedStatementRightNode
) : QualifiedStatementLeftNode, StatementNode
