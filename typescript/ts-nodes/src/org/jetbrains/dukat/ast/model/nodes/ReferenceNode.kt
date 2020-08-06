package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.ReferenceEntity
import org.jetbrains.dukat.tsmodel.ReferenceOriginDeclaration

data class ReferenceNode(override val uid: String, val origin: ReferenceOriginDeclaration = ReferenceOriginDeclaration.IRRELEVANT) : ReferenceEntity