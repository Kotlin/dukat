package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration

data class WhileStatementDeclaration(
    val condition: ExpressionDeclaration,
    val statement: BlockDeclaration
) : StatementDeclaration
