package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.MemberDeclaration

data class InterfaceDeclaration(
        override val name: String,
        val members: List<MemberDeclaration>,
        override val typeParameters: List<TypeParameterDeclaration>,
        val parentEntities: List<HeritageClauseDeclaration>,
        val definitionsInfo: List<DefinitionInfoDeclaration>,
        val uid: String
) : ClassLikeDeclaration