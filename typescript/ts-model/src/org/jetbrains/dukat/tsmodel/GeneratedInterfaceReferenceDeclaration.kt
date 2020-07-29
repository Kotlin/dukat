package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.MetaData
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class GeneratedInterfaceReferenceDeclaration(
        val name: NameEntity,
        val typeParameters: List<ParameterValueDeclaration>,
        override val reference: ReferenceDeclaration?,

        override val nullable: Boolean = false,
        override var meta: MetaData? = null
) : ParameterValueDeclaration, WithReferenceDeclaration