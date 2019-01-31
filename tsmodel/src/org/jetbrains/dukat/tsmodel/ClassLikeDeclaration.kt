package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.types.TopLevelDeclaration

interface ClassLikeDeclaration : TopLevelDeclaration {
    val name: String
    val typeParameters: List<TypeParameterDeclaration>
}