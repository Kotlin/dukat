package org.jetbrains.dukat.ast.model

data class VariableDeclaration(
        val name: String,
        val type: ParameterValue
) : Declaration