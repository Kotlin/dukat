package org.jetbrains.dukat.astModel

data class ConstructorModel(
        val parameters: List<ParameterModel>,
        val typeParameters: List<TypeParameterModel>
) : MemberModel