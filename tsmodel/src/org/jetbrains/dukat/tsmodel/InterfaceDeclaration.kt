package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.AstMemberEntity

data class InterfaceDeclaration(
        override val name: String,
        val members: List<AstMemberEntity>,
        override val typeParameters: List<TypeParameterDeclaration>,
        val parentEntities: List<HeritageClauseDeclaration>,
        val definitionsInfo: List<DefinitionInfoDeclaration>,
        val uid: String
) : ClassLikeDeclaration