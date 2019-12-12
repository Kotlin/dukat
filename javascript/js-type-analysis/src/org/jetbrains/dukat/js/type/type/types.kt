package org.jetbrains.dukat.js.type.type

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

val voidType = TypeDeclaration(
        value = IdentifierEntity("Unit"),
        params = emptyList(),
        nullable = false
)

val nothingType = TypeDeclaration(
        value = IdentifierEntity("Nothing"),
        params = emptyList(),
        nullable = false
)

val anyNullableType = TypeDeclaration(
        value = IdentifierEntity("any"),
        params = emptyList(),
        nullable = true
)

val numberType = TypeDeclaration(
        value = IdentifierEntity("number"),
        params = emptyList(),
        nullable = false
)

val booleanType = TypeDeclaration(
        value = IdentifierEntity("boolean"),
        params = emptyList(),
        nullable = false
)

val stringType = TypeDeclaration(
        value = IdentifierEntity("string"),
        params = emptyList(),
        nullable = false
)