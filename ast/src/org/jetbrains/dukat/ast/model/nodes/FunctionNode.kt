package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.AstMemberEntity
import org.jetbrains.dukat.astCommon.AstTopLevelEntity
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class FunctionNode(
        val name: NameNode,
        val parameters: List<ParameterNode>,
        val type: ParameterValueDeclaration,
        val typeParameters: List<TypeParameterDeclaration>,

        val generatedReferenceNodes: MutableList<GeneratedInterfaceReferenceNode>,
        val annotations: MutableList<AnnotationNode>,

        val export: Boolean,
        val inline: Boolean,
        val operator: Boolean,

        var owner: DocumentRootNode?,
        val body: List<StatementNode>,
        val uid: String
) : AstMemberEntity, AstTopLevelEntity, MergableNode, MemberNode