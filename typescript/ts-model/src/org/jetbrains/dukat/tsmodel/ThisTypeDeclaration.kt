package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.MetaData
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class ThisTypeDeclaration(
        override var nullable: Boolean = false,
        override var meta: MetaData? = null
) : ParameterValueDeclaration