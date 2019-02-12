package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration


data class GenericParamDeclaration(val token: TokenDeclaration) : ParameterValueDeclaration {
    override val nullable: Boolean
        get() = throw Exception("nullable is not reachable in GenericParamDeclaration and exists only for historical reasons")
    override var meta: ParameterValueDeclaration? = null
        get() = throw Exception("meta is not reachable in GenericParamDeclaration and exists only for historical reasons")
}