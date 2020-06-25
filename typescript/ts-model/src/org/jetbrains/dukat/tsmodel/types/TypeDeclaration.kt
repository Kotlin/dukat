package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.astCommon.MetaData
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.ReferenceDeclaration
import org.jetbrains.dukat.tsmodel.WithReferenceDeclaration

data class TypeDeclaration(
        val value: NameEntity,
        val params: List<ParameterValueDeclaration>,
        override val reference: ReferenceDeclaration? = null,
        override var nullable: Boolean = false,
        override var meta: MetaData? = null
) : ParameterValueDeclaration, ParameterOwnerDeclaration, WithReferenceDeclaration