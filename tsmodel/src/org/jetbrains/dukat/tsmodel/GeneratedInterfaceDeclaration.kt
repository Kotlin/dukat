package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.MemberDeclaration

data class GeneratedInterfaceDeclaration(
        override val name: String,
        val members: List<MemberDeclaration>,
        override val typeParameters: List<TypeParameterDeclaration>,
        val parentEntities: List<HeritageClauseDeclaration>
) : ClassLikeDeclaration