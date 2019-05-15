package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.TopLevelEntity

interface ClassLikeDeclaration : TopLevelEntity {
    val name: String
    val typeParameters: List<TypeParameterDeclaration>
}