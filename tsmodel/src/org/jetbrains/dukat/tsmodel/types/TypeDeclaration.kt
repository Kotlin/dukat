package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.tsmodel.IdentifierDeclaration

data class TypeDeclaration(
        val value: IdentifierDeclaration,
        val params: List<ParameterValueDeclaration>,
        override var nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : ParameterValueDeclaration