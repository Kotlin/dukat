package org.jetbrains.dukat.ast.model.declaration.types

data class StringTypeDeclaration(
        val tokens: List<String>,

        override val nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
): ParameterValueDeclaration