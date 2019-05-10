package org.jetbrains.dukat.ast.model.nodes

data class ClassNode(
        val name: String,
        val members: List<MemberNode>,
        val typeParameters: List<TypeValueNode>,
        val parentEntities: List<HeritageNode>,
        val primaryConstructor: ConstructorNode?,

        var owner: DocumentRootNode?,
        val uid: String,
        val annotations: MutableList<AnnotationNode>
) : ClassLikeNode
