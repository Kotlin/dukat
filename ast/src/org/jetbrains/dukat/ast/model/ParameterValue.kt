package org.jetbrains.dukat.ast.model

interface ParameterValue : Declaration {
    val vararg: Boolean
}