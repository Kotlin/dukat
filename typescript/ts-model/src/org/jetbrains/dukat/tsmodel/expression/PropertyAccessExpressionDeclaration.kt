package org.jetbrains.dukat.tsmodel.expression

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration

data class PropertyAccessExpressionDeclaration(
        val expression: ExpressionDeclaration,
        val name: IdentifierEntity
) : ExpressionDeclaration