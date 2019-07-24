package org.jetbrains.dukat.astModel.statements

data class ChainCallModel(
        val left: StatementCallModel,
        val right: StatementCallModel,
        override val metaDescription: String? = null
) : StatementModel
