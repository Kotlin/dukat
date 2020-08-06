package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.BlockDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

data class MethodNode(
        val name: String,
        override val parameters: List<ParameterNode>,
        val type: ParameterValueDeclaration,
        val typeParameters: List<TypeDeclaration>,

        val static: Boolean,
        val operator: Boolean,
        val open: Boolean,

        val body: BlockDeclaration?,
        val isGenerator: Boolean
) : MemberNode, ParameterOwnerNode
