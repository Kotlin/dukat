package org.jetbrains.dukat.ast.model.model

import org.jetbrains.dukat.astCommon.Declaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class HeritageModel(
        var value: ParameterValueDeclaration,
        val delegateTo: DelegationModel?
) : Declaration