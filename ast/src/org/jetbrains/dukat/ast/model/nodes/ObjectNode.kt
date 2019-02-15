package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration

data class ObjectNode(
        val name: String,
        val members: List<MemberNode>,

        val parentEntities: List<HeritageClauseDeclaration>
) : ClassLikeNode
