package org.jetbrains.dukat.ast.model

data class TypeDeclaration(
        val value: String,
        val params: List<ParameterValue>,
        val nullable: Boolean = false
) : ParameterValue {
    constructor(value: String, params: Array<ParameterValue>) : this(value, params.toList())
}