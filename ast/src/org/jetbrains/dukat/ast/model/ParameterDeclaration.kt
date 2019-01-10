package org.jetbrains.dukat.ast.model

data class ParameterDeclaration(
        val name: String,
        val type: ParameterValue,
        val initializer: Expression?
) : Declaration