package org.jetbrains.dukat.ast.model.nodes.metadata

import org.jetbrains.dukat.astCommon.MetaData
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

class IntersectionMetadata(val params: List<ParameterValueDeclaration>) : MetaData