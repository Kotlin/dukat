package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.ast.model.nodes.export.ExportQualifier

data class ClassNode(
        val name: String,
        val members: List<MemberNode>,
        val typeParameters: List<TypeValueNode>,
        val parentEntities: List<HeritageNode>,
        val primaryConstructor: ConstructorNode?,

        val uid: String,
        var exportQualifier: ExportQualifier?
) : ClassLikeNode
