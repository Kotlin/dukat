package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.astCommon.TypeEntity
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.MetaData
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceReferenceDeclaration

interface ParameterValueDeclaration : TypeEntity {
    val nullable: Boolean
    var meta: MetaData?
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

fun ParameterValueDeclaration.makeNullable(): ParameterValueDeclaration {
    return when (this) {
        is IntersectionTypeDeclaration -> copy(nullable = true, params = params.map { it.makeNullable() })
        is NumericLiteralDeclaration -> copy(nullable = true)
        is StringLiteralDeclaration -> copy(nullable = true)
        is TypeDeclaration -> copy(nullable = true)
        is TypeParamReferenceDeclaration -> copy(nullable = true)
        is FunctionTypeDeclaration -> copy(nullable = true)
        is UnionTypeDeclaration -> copy(nullable = true, params = params.map { it.makeNullable() })
        is GeneratedInterfaceReferenceDeclaration -> copy(nullable = true)
        is ObjectLiteralDeclaration -> copy(nullable = true)
        else -> raiseConcern("makeNullable does not recognize type ${this}") { this }
    }
}
