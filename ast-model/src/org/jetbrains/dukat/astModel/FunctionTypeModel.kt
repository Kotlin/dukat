package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class FunctionTypeModel(
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValueDeclaration,

        val metaDescription: String?,
        override var nullable: Boolean = false
) : ParameterValueDeclaration {
    override var meta: ParameterValueDeclaration?
        get() = throw Exception("this exists only for transition period and can not be called")
        set(value) {}
}