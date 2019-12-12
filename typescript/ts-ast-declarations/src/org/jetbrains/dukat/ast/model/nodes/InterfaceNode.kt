package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.ast.model.nodes.export.ExportQualifier
import org.jetbrains.dukat.astCommon.NameEntity

data class InterfaceNode(
        override val name: NameEntity,
        override val members: List<MemberNode>,
        val typeParameters: List<TypeValueNode>,
        val parentEntities: List<HeritageNode>,
        var exportQualifier: ExportQualifier?,

        val generated: Boolean,
        override val uid: String
) : ClassLikeNode