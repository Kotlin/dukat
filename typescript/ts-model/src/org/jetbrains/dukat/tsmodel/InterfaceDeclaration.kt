package org.jetbrains.dukat.tsmodel

import MergeableDeclaration
import org.jetbrains.dukat.astCommon.NameEntity

data class InterfaceDeclaration(
        override val name: NameEntity,
        override val members: List<MemberDeclaration>,
        override val typeParameters: List<TypeParameterDeclaration>,
        override val parentEntities: List<HeritageClauseDeclaration>,
        override val definitionsInfo: List<DefinitionInfoDeclaration>,
        override val uid: String
) : ClassLikeDeclaration, WithUidDeclaration, MergeableDeclaration