package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration

data class CaseDeclaration(
    val condition: ExpressionDeclaration?,
    val body: BlockDeclaration
) : Declaration