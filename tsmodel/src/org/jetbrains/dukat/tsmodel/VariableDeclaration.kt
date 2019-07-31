package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class VariableDeclaration(
        val name: String,
        val type: ParameterValueDeclaration,
        val modifiers: List<ModifierDeclaration>,

        val uid: String
) : TopLevelDeclaration