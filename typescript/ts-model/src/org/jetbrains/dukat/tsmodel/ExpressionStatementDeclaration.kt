package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration

data class ExpressionStatementDeclaration(
        val expression: ExpressionDeclaration
) : StatementDeclaration