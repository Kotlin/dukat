package org.jetbrains.dukat.tsmodel.expression

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class AsExpressionDeclaration(
    val expression: ExpressionDeclaration,
    val type: ParameterValueDeclaration
) : ExpressionDeclaration