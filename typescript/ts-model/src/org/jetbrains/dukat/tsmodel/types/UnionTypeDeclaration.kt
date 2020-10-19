package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.MetaData
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration

data class UnionTypeDeclaration(
        val params: List<ParameterValueDeclaration>,

        override var nullable: Boolean = false,
        override var meta: MetaData? = null
) : ParameterValueDeclaration, ParameterOwnerDeclaration

private fun ParameterValueDeclaration.isString(): Boolean {
    return ((this as? TypeDeclaration)?.value as? IdentifierEntity)?.value == "String"
}

private fun ParameterValueDeclaration.isNumber(): Boolean {
    return ((this as? TypeDeclaration)?.value as? IdentifierEntity)?.value == "Number"
}

fun UnionTypeDeclaration.canBeTranslatedAsStringLiteral(): Boolean {
    val paramsList = params.toMutableList()
    val isStringLiteralUnion =  paramsList.all { it is StringLiteralDeclaration }
    if (isStringLiteralUnion) {
        return true
    }

    paramsList.removeAll { it.isString() }
    return paramsList.all { it is StringLiteralDeclaration }
}

fun UnionTypeDeclaration.canBeTranslatedAsNumericLiteral(): Boolean {
    val paramsList = params.toMutableList()
    val isNumericLiteralUnion =  paramsList.all { it is NumericLiteralDeclaration }
    if (isNumericLiteralUnion) {
        return true
    }

    paramsList.removeAll { it.isNumber() }
    return params.all { it is NumericLiteralDeclaration }
}
