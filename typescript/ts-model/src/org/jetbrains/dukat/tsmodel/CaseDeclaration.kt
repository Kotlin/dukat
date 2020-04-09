package org.jetbrains.dukat.tsmodel

data class CaseDeclaration(
    val condition: ExpressionDeclaration?,
    val body: BlockDeclaration
) : Declaration