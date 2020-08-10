package org.jetbrains.dukat.tsmodel

import MergeableDeclaration
import org.jetbrains.dukat.astCommon.NameEntity

interface ClassLikeDeclaration : TopLevelDeclaration, MemberOwnerDeclaration, MergeableDeclaration {
    val name: NameEntity
    val typeParameters: List<TypeParameterDeclaration>
    override val members: List<MemberDeclaration>
    val parentEntities: List<HeritageClauseDeclaration>
}