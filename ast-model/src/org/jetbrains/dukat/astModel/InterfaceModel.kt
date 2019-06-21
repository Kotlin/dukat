package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.astCommon.IdentifierEntity

data class InterfaceModel(
        override val name: IdentifierEntity,
        val members: List<MemberNode>,
        val companionObject: CompanionObjectModel,
        val typeParameters: List<TypeParameterModel>,
        val parentEntities: List<HeritageModel>,
        val annotations: MutableList<AnnotationNode>
) : ClassLikeNode, ClassLikeModel
