package org.jetbrains.dukat.ast.model.nodes

data class AssignmentStatementNode(
    val left: StatementNode,
    val right: StatementNode
) : StatementNode