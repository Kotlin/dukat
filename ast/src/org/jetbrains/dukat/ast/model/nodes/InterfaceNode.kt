package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration

data class InterfaceNode(
        override val name: String,
        val members: List<MemberDeclaration>,
        override val typeParameters: List<TypeParameterDeclaration>,
        val parentEntities: List<HeritageClauseDeclaration>,
        val annotations: MutableList<AnnotationNode>,

        var owner: DocumentRootNode?,
        val uid: String,
        override val generatedReferenceNodes: MutableList<GeneratedInterfaceReferenceNode> = mutableListOf()
) : ClassLikeDeclaration, ClassLikeNode