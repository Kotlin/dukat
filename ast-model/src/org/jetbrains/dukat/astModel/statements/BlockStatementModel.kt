package org.jetbrains.dukat.astModel.statements

data class BlockStatementModel(
    val statements: List<StatementModel>,
    override val metaDescription: String? = null
): StatementModel