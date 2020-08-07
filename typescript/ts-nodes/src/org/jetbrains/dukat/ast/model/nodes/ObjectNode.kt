package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.MemberDeclaration

data class ObjectNode(
        override val name: NameEntity,
        override val members: List<MemberDeclaration>,

        val parentEntities: List<HeritageNode>,
        override val uid: String,
        override val external: Boolean
) : ClassLikeNode
