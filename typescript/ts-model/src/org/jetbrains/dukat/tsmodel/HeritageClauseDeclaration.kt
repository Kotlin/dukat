package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.ReferenceEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class HeritageClauseDeclaration(
        val name: NameEntity,
        val typeArguments: List<ParameterValueDeclaration>,
        val extending: Boolean,
        val typeReference: ReferenceEntity<ClassLikeDeclaration>?
) : Entity, ParameterOwnerDeclaration