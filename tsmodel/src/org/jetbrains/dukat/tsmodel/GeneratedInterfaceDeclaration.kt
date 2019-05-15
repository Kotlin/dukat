package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.MemberEntity

data class GeneratedInterfaceDeclaration(
        override val name: String,
        val members: List<MemberEntity>,
        override val typeParameters: List<TypeParameterDeclaration>,
        val parentEntities: List<HeritageClauseDeclaration>,
        val uid: String,
        val packageOwner: PackageDeclaration
) : ClassLikeDeclaration