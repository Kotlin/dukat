package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.WithUidDeclaration

interface ClassLikeNode : TopLevelDeclaration, WithUidDeclaration {
    val name: NameEntity
    val members: List<MemberDeclaration>
}