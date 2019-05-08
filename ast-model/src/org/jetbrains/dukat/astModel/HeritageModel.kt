package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.astCommon.AstEntity

data class HeritageModel(
        var value: TypeNode,
        val delegateTo: DelegationModel?
) : AstEntity