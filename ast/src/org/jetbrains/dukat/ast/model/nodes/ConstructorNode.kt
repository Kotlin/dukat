package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration

data class ConstructorNode(
        val parameters: List<ParameterNode>,
        val typeParameters: List<TypeParameterDeclaration>,

        val generated: Boolean = false
) : MemberNode