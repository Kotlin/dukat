package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.ValueTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

interface IdentityLowering : Lowering {
    override fun lowerVariableNode(declaration: VariableNode): VariableNode = declaration

    override fun lowerFunctionNode(declaration: FunctionNode): FunctionNode = declaration

    override fun lowerClassNode(declaration: ClassNode): ClassNode = declaration

    override fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode = declaration

    override fun lowerTypeNode(declaration: ValueTypeNode): ValueTypeNode = declaration

    override fun lowerFunctionNode(declaration: FunctionTypeNode): FunctionTypeNode = declaration

    override fun lowerParameterDeclaration(declaration: ParameterDeclaration): ParameterDeclaration = declaration

    override fun lowerTypeParameter(declaration: TypeParameterDeclaration): TypeParameterDeclaration = declaration

    override fun lowerObjectNode(declaration: ObjectNode) = declaration

    override fun lowerIntersectionTypeDeclaration(declaration: IntersectionTypeDeclaration): IntersectionTypeDeclaration = declaration

    override fun lowerUnionTypeDeclaration(declaration: UnionTypeDeclaration): UnionTypeDeclaration = declaration

    override fun lowerMemberNode(declaration: MemberNode): MemberNode = declaration

    override fun lowerTupleDeclaration(declaration: TupleDeclaration) = declaration

    override fun lowerTypeAliasNode(declaration: TypeAliasNode): TypeAliasNode = declaration

    override fun lowerUnionTypeNode(declaration: UnionTypeNode) = declaration
}
