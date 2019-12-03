package org.jetrbains.dukat.nodeLowering

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

interface IdentityLowering : Lowering<ParameterValueDeclaration> {
    override fun lowerVariableNode(declaration: VariableNode): VariableNode = declaration

    override fun lowerFunctionNode(declaration: FunctionNode): FunctionNode = declaration

    override fun lowerClassNode(declaration: ClassNode): ClassNode = declaration

    override fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode = declaration

    override fun lowerTypeNode(declaration: TypeValueNode): TypeValueNode = declaration

    override fun lowerFunctionNode(declaration: FunctionTypeNode): FunctionTypeNode = declaration

    override fun lowerParameterNode(declaration: ParameterNode): ParameterNode = declaration

    override fun lowerTypeParameter(declaration: TypeValueNode): TypeValueNode = declaration

    override fun lowerObjectNode(declaration: ObjectNode) = declaration

    override fun lowerMemberNode(declaration: MemberNode): MemberNode = declaration

    override fun lowerTypeAliasNode(declaration: TypeAliasNode, owner: DocumentRootNode): TypeAliasNode = declaration

    override fun lowerUnionTypeNode(declaration: UnionTypeNode) = declaration
}
