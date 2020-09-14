package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class ConstructSignatureDeclaration(
    override val parameters: List<ParameterDeclaration>,
    override val type: ParameterValueDeclaration,
    override val typeParameters: List<TypeParameterDeclaration>
) : CallableMemberDeclaration, ParameterOwnerDeclaration, FunctionLikeDeclaration