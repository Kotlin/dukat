package org.jetbrains.dukat.ast.model.declaration

import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration

data class TypeParameterDeclaration(val name: String, val constraints: List<ParameterValueDeclaration>) : Declaration