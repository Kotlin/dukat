package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration

data class InterfaceModel(
        val name: String,
        val members: List<MemberNode>,
        val companionObject: CompanionObjectModel,
        val typeParameters: List<TypeParameterDeclaration>,
        val parentEntities: List<HeritageNode>,
        val annotations: MutableList<AnnotationNode>
) : ClassLikeNode, ClassLikeModel
