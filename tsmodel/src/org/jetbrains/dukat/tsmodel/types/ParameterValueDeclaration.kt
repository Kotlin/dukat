package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.astCommon.TypeEntity
import org.jetbrains.dukat.astCommon.IdentifierEntity

interface ParameterValueDeclaration : TypeEntity {
    val nullable: Boolean
    var meta: ParameterValueDeclaration?
}

fun ParameterValueDeclaration.isSimpleType(str: String): Boolean {
    return this == TypeDeclaration(value = IdentifierEntity(str), params = emptyList(), nullable = false, meta = null)
}