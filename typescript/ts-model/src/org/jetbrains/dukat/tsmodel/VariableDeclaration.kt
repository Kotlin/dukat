package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class VariableDeclaration(
        val name: String,
        val type: ParameterValueDeclaration,
        override val modifiers: List<ModifierDeclaration>,
        val initializer: ExpressionDeclaration?,

        val definitionsInfo: List<DefinitionInfoDeclaration>,
        override val uid: String
) : StatementDeclaration, WithUidDeclaration, ParameterOwnerDeclaration, WithModifiersDeclaration