package org.jetbrains.dukat.ast.model.declaration

import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration

data class VariableDeclaration(
        val name: String,
        val type: ParameterValueDeclaration
) : Declaration