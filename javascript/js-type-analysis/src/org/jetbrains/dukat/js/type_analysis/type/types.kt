package org.jetbrains.dukat.js.type_analysis.type

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

val unitType = TypeDeclaration(
        value = IdentifierEntity("Unit"),
        params = emptyList(),
        nullable = false
)

val anyNullableType = TypeDeclaration(
        value = IdentifierEntity("Any"),
        params = emptyList(),
        nullable = true
)

val numberType = TypeDeclaration(
        value = IdentifierEntity("Number"),
        params = emptyList(),
        nullable = false
)

val booleanType = TypeDeclaration(
        value = IdentifierEntity("Boolean"),
        params = emptyList(),
        nullable = false
)

val stringType = TypeDeclaration(
        value = IdentifierEntity("String"),
        params = emptyList(),
        nullable = false
)