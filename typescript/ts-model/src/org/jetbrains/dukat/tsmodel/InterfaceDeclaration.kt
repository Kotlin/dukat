package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.NameEntity

data class InterfaceDeclaration(
        override val name: NameEntity,
        override val members: List<MemberEntity>,
        override val typeParameters: List<TypeParameterDeclaration>,
        override val parentEntities: List<HeritageClauseDeclaration>,
        val definitionsInfo: List<DefinitionInfoDeclaration>,
        override val uid: String
) : ClassLikeDeclaration, WithUidDeclaration