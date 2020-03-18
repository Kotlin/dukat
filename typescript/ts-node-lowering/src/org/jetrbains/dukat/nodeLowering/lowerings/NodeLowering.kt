package org.jetrbains.dukat.nodeLowering.lowerings

import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.astCommon.Lowering


interface NodeLowering: Lowering<SourceSetNode, SourceSetNode>

fun SourceSetNode.lower(vararg lowerings: NodeLowering): SourceSetNode {
    return lowerings.fold(this) { sourceSet, lowering ->  lowering.lower(sourceSet) }
}
