package org.jetbrains.dukat.astModel.statements

data class AssignmentStatementModel(
        val left: StatementModel,
        val right: StatementModel
) : StatementModel