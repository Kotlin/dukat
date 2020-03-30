package org.jetbrains.dukat.astModel.statements

data class RunBlockStatementModel(
    val statements: List<StatementModel>,
    override val metaDescription: String? = null
): StatementModel