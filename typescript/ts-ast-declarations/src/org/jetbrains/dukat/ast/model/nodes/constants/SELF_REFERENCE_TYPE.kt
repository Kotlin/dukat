package org.jetbrains.dukat.ast.model.nodes.constants

import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.metadata.ThisTypeInGeneratedInterfaceMetaData
import org.jetbrains.dukat.astCommon.IdentifierEntity

val SELF_REFERENCE_TYPE = TypeValueNode(IdentifierEntity("Any"), emptyList(), null, false, ThisTypeInGeneratedInterfaceMetaData())