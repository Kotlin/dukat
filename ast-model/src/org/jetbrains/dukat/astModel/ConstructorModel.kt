package org.jetbrains.dukat.astModel

data class ConstructorModel(
        override val parameters: List<ParameterModel>,
        val typeParameters: List<TypeParameterModel>
) : MemberModel, ParametersOwnerModel<ParameterModel>