package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.expressions.ExpressionModel

data class PropertyModel(
        override val name: NameEntity,
        val type: TypeModel,
        val typeParameters: List<TypeParameterModel>,

        val static: Boolean,
        val override: NameEntity?,
        val immutable: Boolean,

        val initializer: ExpressionModel?,

        val getter: Boolean,
        val setter: Boolean,

        val open: Boolean,

        val explicitlyDeclaredType: Boolean
) : MemberModel, NamedModel