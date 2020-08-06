package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

data class VariableNode(
        var name: NameEntity,
        val type: ParameterValueDeclaration,

        val inline: Boolean,
        val typeParameters: List<TypeDeclaration>,
        val extend: ClassLikeReferenceNode?,
        override val uid: String,
        override val external: Boolean,
        val explicitlyDeclaredType: Boolean
) : TopLevelNode
