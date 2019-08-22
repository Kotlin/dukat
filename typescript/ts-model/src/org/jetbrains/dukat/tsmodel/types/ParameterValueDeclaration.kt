package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.astCommon.TypeEntity
import org.jetbrains.dukat.astCommon.IdentifierEntity

interface ParameterValueDeclaration : TypeEntity {
    val nullable: Boolean
    var meta: ParameterValueDeclaration?
}

fun ParameterValueDeclaration.isSimpleType(str: String): Boolean {
    if (this !is TypeDeclaration) {
        return false
    }

    val referenceTypeDeclaration = TypeDeclaration(value = IdentifierEntity(str), params = emptyList(), nullable = false, meta = null)
    return (value == referenceTypeDeclaration.value)
            && (params == referenceTypeDeclaration.params)
            && (nullable == referenceTypeDeclaration.nullable)
}