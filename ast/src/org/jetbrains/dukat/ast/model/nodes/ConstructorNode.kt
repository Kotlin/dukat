package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.ast.model.declaration.MemberDeclaration
import org.jetbrains.dukat.ast.model.declaration.ParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.TypeParameterDeclaration

data class ConstructorNode(
        val parameters: List<ParameterDeclaration>,
        val typeParameters: List<TypeParameterDeclaration>,

        val generated: Boolean = false
) : MemberDeclaration