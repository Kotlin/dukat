package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.AstEntity

data class AnnotationNode(
        val name: String,
        val params: List<NameNode>
) : AstEntity