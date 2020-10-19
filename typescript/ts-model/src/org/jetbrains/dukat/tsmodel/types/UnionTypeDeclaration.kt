package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.astCommon.MetaData
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration

data class UnionTypeDeclaration(
        val params: List<ParameterValueDeclaration>,

        override var nullable: Boolean = false,
        override var meta: MetaData? = null
) : ParameterValueDeclaration, ParameterOwnerDeclaration

fun UnionTypeDeclaration.canBeTranslatedAsStringLiteral(): Boolean {
    return params.all { it is StringLiteralDeclaration }
}

fun UnionTypeDeclaration.canBeTranslatedAsNumericLiteral(): Boolean {
    return params.all { it is NumericLiteralDeclaration }
}
