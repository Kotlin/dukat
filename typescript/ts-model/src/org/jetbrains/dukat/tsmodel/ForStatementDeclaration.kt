package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration

data class ForStatementDeclaration(
    val initializer: BlockDeclaration,
    val condition: ExpressionDeclaration?,
    val incrementor: ExpressionDeclaration?,
    val body: BlockDeclaration
) : StatementDeclaration