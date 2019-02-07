package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class QualifiedNamedDeclaration(
        val left: ParameterValueDeclaration,
        val right: ParameterValueDeclaration,

        override var nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : ParameterValueDeclaration, ModuleReferenceDeclaration