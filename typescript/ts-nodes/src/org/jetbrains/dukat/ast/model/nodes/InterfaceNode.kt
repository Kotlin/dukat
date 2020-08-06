package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

data class InterfaceNode(
        override val name: NameEntity,
        override val members: List<MemberNode>,
        val typeParameters: List<TypeDeclaration>,
        val parentEntities: List<HeritageNode>,

        val generated: Boolean,
        override val uid: String,
        override val external: Boolean
) : ClassLikeNode