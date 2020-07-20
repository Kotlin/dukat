package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel

data class ConstructorModel(
        val parameters: List<ParameterModel>,
        val typeParameters: List<TypeParameterModel>,
        override val visibilityModifier: VisibilityModifierModel
) : MemberModel