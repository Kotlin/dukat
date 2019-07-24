package org.jetbrains.dukat.idlDeclarations

interface IDLTypeDeclaration : IDLDeclaration {
    val name: String
    val nullable: Boolean
}

fun IDLTypeDeclaration.toNullable(): IDLTypeDeclaration {
    return when (this) {
        is IDLSingleTypeDeclaration -> copy(nullable = true)
        is IDLUnionTypeDeclaration -> copy(nullable = true)
        is IDLFunctionTypeDeclaration -> copy(nullable = true)
        else -> this
    }
}