package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.astCommon.IdentifierEntity

data class InterfaceModel(
        override val name: IdentifierEntity,
        override val members: List<MemberModel>,
        override val companionObject: CompanionObjectModel,
        val typeParameters: List<TypeParameterModel>,
        val parentEntities: List<HeritageModel>,
        val annotations: MutableList<AnnotationNode>,
        val external: Boolean
) : ClassLikeModel
