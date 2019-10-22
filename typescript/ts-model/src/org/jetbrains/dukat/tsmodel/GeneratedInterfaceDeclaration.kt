package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.NameEntity

data class GeneratedInterfaceDeclaration(
        override val name: NameEntity,
        override val members: List<MemberDeclaration>,
        override val typeParameters: List<TypeParameterDeclaration>,
        override val parentEntities: List<HeritageClauseDeclaration>,
        override val uid: String,
        val packageOwner: ModuleDeclaration
) : ClassLikeDeclaration, WithUidDeclaration