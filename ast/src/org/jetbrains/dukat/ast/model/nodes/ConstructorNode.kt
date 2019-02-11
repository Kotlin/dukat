package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration

data class ConstructorNode(
        val parameters: List<ParameterDeclaration>,
        val typeParameters: List<TypeParameterDeclaration>,

        val generated: Boolean = false
) : MemberNode