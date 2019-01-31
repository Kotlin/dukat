package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

interface IdentityLowering : Lowering {
    override fun lowerVariableDeclaration(declaration: VariableDeclaration): VariableDeclaration = declaration

    override fun lowerFunctionNode(declaration: FunctionNode): FunctionNode = declaration

    override fun lowerClassNode(declaration: ClassNode): ClassNode = declaration

    override fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode = declaration

    override fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration = declaration

    override fun lowerFunctionTypeDeclaration(declaration: FunctionTypeDeclaration): FunctionTypeDeclaration = declaration

    override fun lowerParameterDeclaration(declaration: ParameterDeclaration): ParameterDeclaration = declaration

    override fun lowerTypeParameter(declaration: TypeParameterDeclaration): TypeParameterDeclaration = declaration

    override fun lowerObjectLiteral(declaration: ObjectLiteralDeclaration): ObjectLiteralDeclaration = declaration

    override fun lowerUnionTypeDeclation(declaration: UnionTypeDeclaration): UnionTypeDeclaration = declaration

    override fun lowerMemberDeclaration(declaration: MemberDeclaration): MemberDeclaration = declaration

    override fun lowerTypeAliasDeclaration(declaration: TypeAliasDeclaration): TypeAliasDeclaration = declaration
}
