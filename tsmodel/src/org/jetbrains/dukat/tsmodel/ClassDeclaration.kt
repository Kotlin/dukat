package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.AstMemberEntity

data class ClassDeclaration(
        override val name: String,
        val members: List<AstMemberEntity>,
        override val typeParameters: List<TypeParameterDeclaration>,
        val parentEntities: List<HeritageClauseDeclaration>,
        val modifiers: List<ModifierDeclaration>,
        val uid: String
) : ClassLikeDeclaration
