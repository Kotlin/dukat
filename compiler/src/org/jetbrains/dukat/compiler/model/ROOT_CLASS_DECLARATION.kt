package org.jetbrains.dukat.compiler.model

import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.GeneratedInterfaceReferenceNode

object ROOT_CLASS_DECLARATION : ClassLikeNode {
    override val generatedReferenceNodes: MutableList<GeneratedInterfaceReferenceNode> = mutableListOf()
}