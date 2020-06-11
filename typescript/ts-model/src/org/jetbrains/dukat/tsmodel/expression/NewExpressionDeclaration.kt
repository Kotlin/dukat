package org.jetbrains.dukat.tsmodel.expression

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class NewExpressionDeclaration(
    val expression: ExpressionDeclaration,
    val arguments: List<ExpressionDeclaration>,
    val typeArguments: List<ParameterValueDeclaration>
) : ExpressionDeclaration