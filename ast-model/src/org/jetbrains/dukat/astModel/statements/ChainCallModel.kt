package org.jetbrains.dukat.astModel.statements

data class ChainCallModel(
        val left: StatementCallModel,
        val right: StatementCallModel
) : StatementModel
