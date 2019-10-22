package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.NameEntity

data class ClassDeclaration(
        override val name: NameEntity,
        override val members: List<MemberEntity>,
        override val typeParameters: List<TypeParameterDeclaration>,
        override val parentEntities: List<HeritageClauseDeclaration>,
        val modifiers: List<ModifierDeclaration>,
        override val uid: String
) : ClassLikeDeclaration, WithUidDeclaration
