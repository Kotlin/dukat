package org.jetbrains.dukat.tsmodel.types

data class StringLiteralDeclaration(
        val token: String,

        override val nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
): ParameterValueDeclaration