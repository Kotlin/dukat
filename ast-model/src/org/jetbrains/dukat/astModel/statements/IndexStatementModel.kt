package org.jetbrains.dukat.astModel.statements

class IndexStatementModel(
        val array: StatementCallModel,
        val index: StatementCallModel
): StatementModel