package org.jetbrains.dukat.tsmodel

data class ForStatementDeclaration(
    val initializer: BlockDeclaration,
    val condition: ExpressionDeclaration,
    val incrementor: ExpressionDeclaration,
    val body: BlockDeclaration
) : StatementDeclaration