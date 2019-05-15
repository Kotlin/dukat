package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class TypeAliasNode(
        val name: NameNode,
        val typeReference: ParameterValueDeclaration,
        val typeParameters: List<IdentifierNode>,

        var canBeTranslated: Boolean
) : TopLevelEntity, TopLevelNode