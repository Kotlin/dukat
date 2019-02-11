package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.MemberDeclaration

data class ObjectNode(
        val name: String,
        val members: List<MemberDeclaration>
) : ClassLikeNode
