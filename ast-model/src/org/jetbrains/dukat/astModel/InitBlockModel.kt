package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astModel.statements.BlockStatementModel

data class InitBlockModel(
    val body: BlockStatementModel
): MemberModel