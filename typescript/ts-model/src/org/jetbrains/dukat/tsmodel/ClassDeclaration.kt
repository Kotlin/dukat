package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.NameEntity

data class ClassDeclaration(
        override val name: NameEntity,
        val members: List<MemberEntity>,
        override val typeParameters: List<TypeParameterDeclaration>,
        val parentEntities: List<HeritageClauseDeclaration>,
        val modifiers: List<ModifierDeclaration>,
        val uid: String
) : ClassLikeDeclaration
