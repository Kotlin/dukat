package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

interface FunctionLikeDeclaration {
    val parameters: List<ParameterDeclaration>
    val type: ParameterValueDeclaration
    val typeParameters: List<TypeParameterDeclaration>
}