package org.jetbrains.dukat.ast.model

data class ClassDeclaration(
        override val name: String,
        val members: List<MemberDeclaration>,
        override val typeParameters: List<TypeParameter>,
        val parentEntities: List<ClassLikeDeclaration>,
        val primaryConstructor: MethodDeclaration? = null
) : ClassLikeDeclaration
