package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.BlockDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

data class ConstructorNode(
        override val parameters: List<ParameterNode>,
        val typeParameters: List<TypeDeclaration>,
        val body: BlockDeclaration?
) : MemberNode, ParameterOwnerNode