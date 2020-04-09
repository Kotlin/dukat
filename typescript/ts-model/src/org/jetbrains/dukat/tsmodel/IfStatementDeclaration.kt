package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration

data class IfStatementDeclaration(
    val condition: ExpressionDeclaration,
    val thenStatement: BlockDeclaration,
    val elseStatement: BlockDeclaration?
) : StatementDeclaration
