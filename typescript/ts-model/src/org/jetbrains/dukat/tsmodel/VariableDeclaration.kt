package org.jetbrains.dukat.tsmodel

import MergeableDeclaration
import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class VariableDeclaration(
    val name: String,
    val type: ParameterValueDeclaration,
    override val modifiers: List<ModifierDeclaration>,
    val initializer: ExpressionDeclaration?,

    override val definitionsInfo: List<DefinitionInfoDeclaration>,
    override val uid: String
) : StatementDeclaration, ParameterOwnerDeclaration, WithModifiersDeclaration, MergeableDeclaration