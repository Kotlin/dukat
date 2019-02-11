package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration

data class ClassNode(
        override val name: String,
        val members: List<MemberNode>,
        override val typeParameters: List<TypeParameterDeclaration>,
        val parentEntities: List<HeritageClauseDeclaration>,
        val primaryConstructor: ConstructorNode?,

        var owner: DocumentRootNode?,
        val uid: String,
        val annotations: MutableList<AnnotationNode>
) : ClassLikeDeclaration, ClassLikeNode
