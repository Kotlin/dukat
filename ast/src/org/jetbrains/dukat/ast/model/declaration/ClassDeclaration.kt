package org.jetbrains.dukat.ast.model.declaration

data class ClassDeclaration(
        override val name: String,
        val members: List<MemberDeclaration>,
        override val typeParameters: List<TypeParameterDeclaration>,
        val parentEntities: List<ClassLikeDeclaration>,
        val primaryConstructor: ConstructorDeclaration? = null,
        val staticMembers: List<MemberDeclaration>
) : ClassLikeDeclaration
