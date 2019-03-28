package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration

data class ConstructorModel(
        val parameters: List<ParameterDeclaration>,
        val typeParameters: List<TypeParameterDeclaration>,

        val generated: Boolean = false
) : MemberNode