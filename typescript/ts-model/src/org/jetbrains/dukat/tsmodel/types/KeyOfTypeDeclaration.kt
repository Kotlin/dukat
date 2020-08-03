package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.astCommon.MetaData
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration

data class KeyOfTypeDeclaration(val type: ParameterValueDeclaration) : ParameterValueDeclaration,
    ParameterOwnerDeclaration {
    override val nullable: Boolean = false
    override var meta: MetaData? = null
}
