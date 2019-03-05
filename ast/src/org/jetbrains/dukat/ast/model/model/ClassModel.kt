package org.jetbrains.dukat.ast.model.model

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration

data class ClassModel(
        val name: String,
        var members: List<MemberNode>,
        val companionObject: CompanionObjectModel,
        val typeParameters: List<TypeParameterDeclaration>,
        val parentEntities: List<HeritageNode>,
        val primaryConstructor: ConstructorNode?,
        val annotations: MutableList<AnnotationNode>


) : ClassLikeNode, ClassLikeModel, DelegationModel, MemberNode
