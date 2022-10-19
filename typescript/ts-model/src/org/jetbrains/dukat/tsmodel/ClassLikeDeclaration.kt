package org.jetbrains.dukat.tsmodel

import MergeableDeclaration
import org.jetbrains.dukat.astCommon.NameEntity

interface ClassLikeDeclaration : TopLevelDeclaration, MemberOwnerDeclaration, MergeableDeclaration {
    val name: NameEntity
    val typeParameters: List<TypeParameterDeclaration>
    override val members: List<MemberDeclaration>
    val parentEntities: List<HeritageClauseDeclaration>
}

fun ClassLikeDeclaration.copy(
    parentEntities: List<HeritageClauseDeclaration> = this.parentEntities,
    typeParameters: List<TypeParameterDeclaration> = this.typeParameters,
    members: List<MemberDeclaration> = this.members
): ClassLikeDeclaration {
    return when (this) {
        is ClassDeclaration -> copy(members = members, parentEntities = parentEntities, typeParameters = typeParameters)
        is InterfaceDeclaration -> copy(members = members, parentEntities = parentEntities, typeParameters = typeParameters)
        is GeneratedInterfaceDeclaration -> copy(members = members, parentEntities = parentEntities, typeParameters = typeParameters)
        else -> this
    }
}