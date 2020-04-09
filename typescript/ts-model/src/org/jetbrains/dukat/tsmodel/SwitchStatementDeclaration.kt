package org.jetbrains.dukat.tsmodel

data class SwitchStatementDeclaration(
    val expression: ExpressionDeclaration,
    val cases: List<CaseDeclaration>
) : StatementDeclaration