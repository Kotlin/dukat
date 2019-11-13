package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class TypeAliasNode(
        val name: NameEntity,
        val typeReference: ParameterValueDeclaration,
        val typeParameters: List<NameEntity>,
        val uid: String
) : TopLevelEntity