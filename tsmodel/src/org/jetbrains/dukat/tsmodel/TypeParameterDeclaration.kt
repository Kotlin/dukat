package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class TypeParameterDeclaration(val name: NameEntity, val constraints: List<ParameterValueDeclaration>) : Entity