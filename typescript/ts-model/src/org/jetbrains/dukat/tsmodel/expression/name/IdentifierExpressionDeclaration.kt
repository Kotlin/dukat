package org.jetbrains.dukat.tsmodel.expression.name

import org.jetbrains.dukat.astCommon.IdentifierEntity

data class IdentifierExpressionDeclaration(
        val identifier: IdentifierEntity
) : NameExpressionDeclaration