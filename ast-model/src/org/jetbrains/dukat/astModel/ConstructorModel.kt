package org.jetbrains.dukat.astModel

data class ConstructorModel(
        val parameters: List<CallableParameterModel>,
        val typeParameters: List<TypeParameterModel>
) : MemberModel