package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.astCommon.NameEntity

data class TypeDeclaration(
        val value: NameEntity,
        val params: List<ParameterValueDeclaration>,
        val typeReference: String? = null,
        override var nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : ParameterValueDeclaration