package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.ast.model.TopLevelNode
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class TypeAliasNode(
        val name: NameEntity,
        val typeReference: ParameterValueDeclaration,
        val typeParameters: List<TypeValueNode>,
        override val uid: String,
        override val external: Boolean
) : TopLevelNode