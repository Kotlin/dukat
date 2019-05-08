package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.AstTopLevelEntity

interface ClassLikeDeclaration : AstTopLevelEntity {
    val name: String
    val typeParameters: List<TypeParameterDeclaration>
}