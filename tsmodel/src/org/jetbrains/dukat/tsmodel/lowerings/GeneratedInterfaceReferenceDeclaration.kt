package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class GeneratedInterfaceReferenceDeclaration(
        val name: NameEntity,
        val typeParameters: List<TypeParameterDeclaration>,

        override val nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : ParameterValueDeclaration