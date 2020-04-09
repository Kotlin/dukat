package org.jetbrains.dukat.tsmodel.expression

import org.jetbrains.dukat.astCommon.IdentifierEntity

data class PropertyAccessExpressionDeclaration(
    val expression: ExpressionDeclaration,
    val name: IdentifierEntity
) : ExpressionDeclaration