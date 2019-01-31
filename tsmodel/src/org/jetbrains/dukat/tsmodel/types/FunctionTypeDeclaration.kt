package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.tsmodel.ParameterDeclaration


data class FunctionTypeDeclaration(
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValueDeclaration,
        override var nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : ParameterValueDeclaration
