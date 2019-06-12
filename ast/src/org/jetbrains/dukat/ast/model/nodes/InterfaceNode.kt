package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.ast.model.nodes.export.ExportQualifier

data class InterfaceNode(
        val name: String,
        val members: List<MemberNode>,
        val typeParameters: List<TypeValueNode>,
        val parentEntities: List<HeritageNode>,
        var exportQualifier: ExportQualifier?,

        var owner: DocumentRootNode?,
        val generated: Boolean,
        val uid: String
) : ClassLikeNode