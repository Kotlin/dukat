package org.jetbrains.dukat.astModel.statements

data class ReturnStatementModel(
        val statement: StatementModel,
        override val metaDescription: String? = null
): StatementModel