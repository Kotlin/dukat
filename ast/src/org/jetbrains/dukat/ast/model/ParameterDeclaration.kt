package org.jetbrains.dukat.ast.model

data class ParameterDeclaration(
        val name: String,
        val type: TypeDeclaration,
        val initializer: Expression?
) : Declaration