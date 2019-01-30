package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.declaration.ClassDeclaration
import org.jetbrains.dukat.ast.model.declaration.FunctionDeclaration
import org.jetbrains.dukat.ast.model.declaration.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.declaration.MemberDeclaration
import org.jetbrains.dukat.ast.model.declaration.ParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.TypeAliasDeclaration
import org.jetbrains.dukat.ast.model.declaration.TypeParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.VariableDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.UnionTypeDeclaration

interface IdentityLowering : Lowering {
    override fun lowerVariableDeclaration(declaration: VariableDeclaration): VariableDeclaration = declaration

    override fun lowerFunctionDeclaration(declaration: FunctionDeclaration): FunctionDeclaration = declaration

    override fun lowerClassDeclaration(declaration: ClassDeclaration): ClassDeclaration = declaration

    override fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration): InterfaceDeclaration = declaration

    override fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration = declaration

    override fun lowerFunctionTypeDeclaration(declaration: FunctionTypeDeclaration): FunctionTypeDeclaration = declaration

    override fun lowerParameterDeclaration(declaration: ParameterDeclaration): ParameterDeclaration = declaration

    override fun lowerTypeParameter(declaration: TypeParameterDeclaration): TypeParameterDeclaration = declaration

    override fun lowerObjectLiteral(declaration: ObjectLiteralDeclaration): ObjectLiteralDeclaration = declaration

    override fun lowerUnionTypeDeclation(declaration: UnionTypeDeclaration): UnionTypeDeclaration = declaration

    override fun lowerMemberDeclaration(declaration: MemberDeclaration): MemberDeclaration = declaration

    override fun lowerTypeAliasDeclaration(declaration: TypeAliasDeclaration): TypeAliasDeclaration = declaration
}
