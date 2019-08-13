package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.NameEntity

data class InterfaceModel(
        override val name: NameEntity,
        override val members: List<MemberModel>,
        override val companionObject: CompanionObjectModel?,
        val typeParameters: List<TypeParameterModel>,
        val parentEntities: List<HeritageModel>,
        val annotations: MutableList<AnnotationModel>,
        val documentation: String?,
        val external: Boolean
) : ClassLikeModel
