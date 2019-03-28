package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration

data class ConstructorModel(
        val parameters: List<ParameterModel>,
        val typeParameters: List<TypeParameterModel>,

        val generated: Boolean = false
) : MemberNode, MemberModel