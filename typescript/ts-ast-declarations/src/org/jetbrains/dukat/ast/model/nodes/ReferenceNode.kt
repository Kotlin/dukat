package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.ReferenceEntity

data class ReferenceNode(override val uid: String, val origin: ReferenceOriginNode = ReferenceOriginNode.IRRELEVANT) : ReferenceEntity