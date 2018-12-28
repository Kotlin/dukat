package org.jetbrains.dukat.ast.model

data class Expression(
    val kind: TypeDeclaration,
    val meta: String?
) : Declaration