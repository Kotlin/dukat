package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode

data class ClassModel(
        val name: String,
        var members: List<MemberNode>,
        val companionObject: CompanionObjectModel,
        val typeParameters: List<TypeParameterModel>,
        val parentEntities: List<HeritageNode>,
        val primaryConstructor: ConstructorModel?,
        val annotations: MutableList<AnnotationNode>


) : ClassLikeNode, ClassLikeModel, DelegationModel, MemberNode, MemberModel
