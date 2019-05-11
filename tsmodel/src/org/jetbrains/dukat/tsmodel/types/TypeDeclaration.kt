package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.tsmodel.QualifiedLeftDeclaration

data class TypeDeclaration(
        val value: QualifiedLeftDeclaration,
        val params: List<ParameterValueDeclaration>,
        override var nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : ParameterValueDeclaration