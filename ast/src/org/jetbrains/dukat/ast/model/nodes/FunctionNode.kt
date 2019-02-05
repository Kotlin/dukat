package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class FunctionNode(
        val name: String,
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValueDeclaration,
        val typeParameters: List<TypeParameterDeclaration>,

        val generatedReferenceNodes: MutableList<GeneratedInterfaceReferenceNode>,
        val annotations: MutableList<AnnotationNode>,

        val uid: String
) : MemberDeclaration, TopLevelDeclaration