package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astModel.modifiers.ExpectActualModifier

data class ConstructorModel(
        override val parameters: List<ParameterModel>,
        val typeParameters: List<TypeParameterModel>,
        override val expectActualModifier: ExpectActualModifier? = null
) : MemberModel, CanBeExpectActual, ParametersOwnerModel<ParameterModel>