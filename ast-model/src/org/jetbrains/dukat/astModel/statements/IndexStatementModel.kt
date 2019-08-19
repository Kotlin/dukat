package org.jetbrains.dukat.astModel.statements

data class IndexStatementModel(
        val array: StatementCallModel,
        val index: StatementCallModel,
        override val metaDescription: String? = null
): StatementModel