package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.astCommon.IdentifierEntity

data class ClassModel(
        override val name: IdentifierEntity,
        override var members: List<MemberNode>,
        override val companionObject: CompanionObjectModel,
        val typeParameters: List<TypeParameterModel>,
        val parentEntities: List<HeritageModel>,
        val primaryConstructor: ConstructorModel?,
        val annotations: MutableList<AnnotationNode>
) : ClassLikeModel, DelegationModel, MemberNode, MemberModel
