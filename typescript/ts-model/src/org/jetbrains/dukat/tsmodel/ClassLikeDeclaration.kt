package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.NameEntity

interface ClassLikeDeclaration : TopLevelDeclaration, FunctionOwnerDeclaration, WithUidDeclaration {
    val name: NameEntity
    val typeParameters: List<TypeParameterDeclaration>
    val members: List<MemberDeclaration>
    val parentEntities: List<HeritageClauseDeclaration>
}