package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity

interface ClassLikeDeclaration : TopLevelEntity {
    val name: NameEntity
    val typeParameters: List<TypeParameterDeclaration>
}