package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration

data class ObjectNode(
        override val name: NameEntity,
        override val members: List<MemberDeclaration>,

        val parentEntities: List<HeritageClauseDeclaration>,
        override val uid: String,
        val external: Boolean
) : ClassLikeNode
