package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.MetaData
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class TypePredicateDeclaration(val parameter: String, val type: ParameterValueDeclaration) : ParameterValueDeclaration {
    override val nullable = false
    override var meta: MetaData? = null
}