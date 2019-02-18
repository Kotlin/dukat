package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.Declaration
import org.jetbrains.dukat.tsmodel.IdentifierDeclaration


data class HeritageNode(
        val name: HeritageSymbolNode,
        val typeArguments: List<IdentifierDeclaration>
) : Declaration
