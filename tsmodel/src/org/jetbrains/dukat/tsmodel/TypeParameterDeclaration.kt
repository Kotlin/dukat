package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.Declaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class TypeParameterDeclaration(val name: String, val constraints: List<ParameterValueDeclaration>) : Declaration