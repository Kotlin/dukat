package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.astCommon.MetaData
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration


data class FunctionTypeDeclaration(
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValueDeclaration,
        override var nullable: Boolean = false,
        override var meta: MetaData? = null
) : ParameterValueDeclaration, ParameterOwnerDeclaration
