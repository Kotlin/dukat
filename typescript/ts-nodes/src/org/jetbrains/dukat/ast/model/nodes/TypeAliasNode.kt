package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

data class TypeAliasNode(
        val name: NameEntity,
        val typeReference: ParameterValueDeclaration,
        val typeParameters: List<TypeDeclaration>,
        override val uid: String,
        override val external: Boolean
) : TopLevelNode