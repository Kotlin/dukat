package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.TopLevelDeclaration

interface ClassLikeDeclaration : TopLevelDeclaration {
    val name: String
    val typeParameters: List<TypeParameterDeclaration>
}