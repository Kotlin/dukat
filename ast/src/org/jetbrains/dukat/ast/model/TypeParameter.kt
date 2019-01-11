package org.jetbrains.dukat.ast.model

data class TypeParameter(val name: String, val constraints: List<ParameterValue>) : Declaration {
    constructor(name: String, constraints: Array<ParameterValue>) : this(name, constraints.toList())
}