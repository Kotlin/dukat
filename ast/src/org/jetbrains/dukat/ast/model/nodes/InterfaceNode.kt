package org.jetbrains.dukat.ast.model.nodes

data class InterfaceNode(
        val name: String,
        val members: List<MemberNode>,
        val typeParameters: List<TypeValueNode>,
        val parentEntities: List<HeritageNode>,
        val annotations: MutableList<AnnotationNode>,

        var owner: DocumentRootNode?,
        val generated: Boolean,
        val uid: String
) : ClassLikeNode