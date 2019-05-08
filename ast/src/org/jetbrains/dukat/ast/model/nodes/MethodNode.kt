package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class MethodNode(
        val name: String,
        val parameters: List<ParameterNode>,
        val type: ParameterValueDeclaration,
        val typeParameters: List<TypeParameterDeclaration>,

        var owner: ClassLikeNode,
        val static: Boolean,
        val override: Boolean,
        val operator: Boolean,
        val annotations: List<AnnotationNode>,

        val open: Boolean,
        val definedExternally: Boolean
) : MemberNode
