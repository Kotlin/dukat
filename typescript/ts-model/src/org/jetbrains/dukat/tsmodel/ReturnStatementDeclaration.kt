package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration

data class ReturnStatementDeclaration(
        val expression: ExpressionDeclaration?
) : StatementDeclaration