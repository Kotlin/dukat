package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TopLevelDeclaration

data class VariableDeclaration(
        val name: String,
        val type: ParameterValueDeclaration
) : TopLevelDeclaration