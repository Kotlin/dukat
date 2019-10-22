package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.NameEntity

interface ClassLikeDeclaration : TopLevelDeclaration, FunctionOwnerDeclaration {
    val name: NameEntity
    val typeParameters: List<TypeParameterDeclaration>
    val members: List<MemberEntity>
    val parentEntities: List<HeritageClauseDeclaration>
}