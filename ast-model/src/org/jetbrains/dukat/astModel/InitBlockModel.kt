package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.astModel.statements.BlockStatementModel

data class InitBlockModel(
    val body: BlockStatementModel
): MemberModel {
    override val visibilityModifier: VisibilityModifierModel = VisibilityModifierModel.DEFAULT
}