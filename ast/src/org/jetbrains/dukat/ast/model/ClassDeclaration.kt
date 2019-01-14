package org.jetbrains.dukat.ast.model

data class ClassDeclaration(
        val name: String,
        val members: List<MemberDeclaration>,
        val typeParameters: List<TypeParameter>,
        val primaryConstructor: FunctionDeclaration? = null
) : Declaration
