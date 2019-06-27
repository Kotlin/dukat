package org.jetbrains.dukat.ast.model.nodes.statements

data class AssignmentStatementNode(
        val left: StatementNode,
        val right: StatementNode
) : StatementNode