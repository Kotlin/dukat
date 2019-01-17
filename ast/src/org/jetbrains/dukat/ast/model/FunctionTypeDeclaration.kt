package org.jetbrains.dukat.ast.model

data class FunctionTypeDeclaration(
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValue,
        override var nullable: Boolean = false,
        override var vararg: Boolean = false,
        override var meta: ParameterValue? = null
) : ParameterValue
