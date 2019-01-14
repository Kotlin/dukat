package org.jetbrains.dukat.ast.model

data class InterfaceDeclaration(
    val name: String,
    val members: List<MemberDeclaration>,
    val typeParameters: List<TypeParameter>
) : Declaration