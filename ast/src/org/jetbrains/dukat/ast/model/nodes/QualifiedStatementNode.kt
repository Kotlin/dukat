package org.jetbrains.dukat.ast.model.nodes

data class QualifiedStatementNode(
    val left: StatementNode,
    val right:  StatementNode
) : StatementNode
