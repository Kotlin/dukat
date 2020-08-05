package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.ExportQualifier
import org.jetbrains.dukat.astCommon.NameEntity

data class ClassNode(
        override val name: NameEntity,
        override val members: List<MemberNode>,
        val typeParameters: List<TypeValueNode>,
        val parentEntities: List<HeritageNode>,

        override val uid: String,
        override val exportQualifier: ExportQualifier?,
        override val external: Boolean
) : ClassLikeNode, ExportableNode
