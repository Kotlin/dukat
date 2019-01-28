package org.jetbrains.dukat.ast.model.declaration.types

import org.jetbrains.dukat.ast.model.declaration.ParameterDeclaration

data class FunctionTypeDeclaration(
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValueDeclaration,
        override var nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : ParameterValueDeclaration
