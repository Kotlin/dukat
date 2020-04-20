package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration

data class SwitchStatementDeclaration(
    val expression: ExpressionDeclaration,
    val cases: List<CaseDeclaration>
) : StatementDeclaration