package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.MemberDeclaration

interface ClassLikeNode : TopLevelNode, UniqueNode {
    val name: NameEntity
    val members: List<MemberDeclaration>
}