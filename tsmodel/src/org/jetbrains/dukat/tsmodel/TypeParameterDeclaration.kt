package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.AstEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class TypeParameterDeclaration(val name: QualifiedLeftDeclaration, val constraints: List<ParameterValueDeclaration>) : AstEntity