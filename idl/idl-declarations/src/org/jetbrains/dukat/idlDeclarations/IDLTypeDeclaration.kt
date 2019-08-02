package org.jetbrains.dukat.idlDeclarations

interface IDLTypeDeclaration : IDLDeclaration {
    val name: String
    val nullable: Boolean
    val comment: String?
}

fun IDLTypeDeclaration.toNullable(): IDLTypeDeclaration {
    return when (this) {
        is IDLSingleTypeDeclaration -> copy(nullable = true)
        is IDLUnionTypeDeclaration -> copy(nullable = true)
        is IDLFunctionTypeDeclaration -> copy(nullable = true)
        else -> this
    }
}

fun IDLTypeDeclaration.toNotNullable(): IDLTypeDeclaration {
    return when (this) {
        is IDLSingleTypeDeclaration -> copy(nullable = false)
        is IDLUnionTypeDeclaration -> copy(nullable = false)
        is IDLFunctionTypeDeclaration -> copy(nullable = false)
        else -> this
    }
}

fun IDLTypeDeclaration.toNullableIfNotPrimitive(): IDLTypeDeclaration {
    return when (this) {
        is IDLSingleTypeDeclaration -> if (isPrimitive()) this else toNullable()
        else -> toNullable()
    }
}

fun IDLTypeDeclaration.changeComment(newComment: String?): IDLTypeDeclaration {
    return when (this) {
        is IDLSingleTypeDeclaration -> copy(comment = newComment)
        is IDLUnionTypeDeclaration -> copy(comment = newComment)
        is IDLFunctionTypeDeclaration -> copy(comment = newComment)
        else -> this
    }
}