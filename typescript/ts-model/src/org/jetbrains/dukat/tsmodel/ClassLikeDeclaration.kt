package org.jetbrains.dukat.tsmodel

import MergeableDeclaration
import org.jetbrains.dukat.astCommon.NameEntity

interface ClassLikeDeclaration : TopLevelDeclaration, MemberOwnerDeclaration, MergeableDeclaration {
    val name: NameEntity
    val typeParameters: List<TypeParameterDeclaration>
    override val members: List<MemberDeclaration>
    val parentEntities: List<HeritageClauseDeclaration>
}

fun ClassLikeDeclaration.copy(newMembers: List<MemberDeclaration>): ClassLikeDeclaration {
    return when (this) {
        is ClassDeclaration -> copy(members = newMembers)
        is InterfaceDeclaration -> copy(members = newMembers)
        is GeneratedInterfaceDeclaration -> copy(members = newMembers)
        else -> this
    }
}