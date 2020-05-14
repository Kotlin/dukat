package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration

data class ThrowStatementDeclaration(
        val expression: ExpressionDeclaration
) : StatementDeclaration