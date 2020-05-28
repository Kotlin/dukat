package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration

data class ForOfStatementDeclaration(
    val variable: VariableLikeDeclaration,
    val expression: ExpressionDeclaration,
    val body: BlockDeclaration
) : StatementDeclaration