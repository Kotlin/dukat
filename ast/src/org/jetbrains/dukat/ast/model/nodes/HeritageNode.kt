package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.AstEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration


data class HeritageNode(
        val name: HeritageSymbolNode,
        val typeArguments: List<ParameterValueDeclaration>
) : AstEntity
