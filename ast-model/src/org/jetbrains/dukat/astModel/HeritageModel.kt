package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Declaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class HeritageModel(
        var value: ParameterValueDeclaration,
        val delegateTo: DelegationModel?
) : Declaration