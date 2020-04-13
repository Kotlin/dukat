package org.jetbrains.dukat.tsLowerings

import TopLevelDeclarationLowering
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.Declaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.FunctionOwnerDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.MemberOwnerDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

interface DeclarationLowering : TopLevelDeclarationLowering {
    fun lowerTypeDeclaration(declaration: TypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): TypeDeclaration
    fun lowerFunctionTypeDeclaration(declaration: FunctionTypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): FunctionTypeDeclaration
    fun lowerParameterDeclaration(declaration: ParameterDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): ParameterDeclaration
    fun lowerTypeParameter(declaration: TypeParameterDeclaration, owner: NodeOwner<Declaration>?): TypeParameterDeclaration
    fun lowerUnionTypeDeclaration(declaration: UnionTypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): UnionTypeDeclaration
    fun lowerTupleDeclaration(declaration: TupleDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): TupleDeclaration
    fun lowerIntersectionTypeDeclaration(declaration: IntersectionTypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): IntersectionTypeDeclaration
    fun lowerMemberDeclaration(declaration: MemberDeclaration, owner: NodeOwner<MemberOwnerDeclaration>?): MemberDeclaration
    fun lowerMethodSignatureDeclaration(declaration: MethodSignatureDeclaration, owner: NodeOwner<MemberDeclaration>?): MethodSignatureDeclaration
    fun lowerIndexSignatureDeclaration(declaration: IndexSignatureDeclaration, owner: NodeOwner<MemberDeclaration>?): IndexSignatureDeclaration
    fun lowerObjectLiteralDeclaration(declaration: ObjectLiteralDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): ObjectLiteralDeclaration

    fun lowerParameterValue(declaration: ParameterValueDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): ParameterValueDeclaration {
        return when (declaration) {
            is TypeDeclaration -> lowerTypeDeclaration(declaration, owner)
            is FunctionTypeDeclaration -> lowerFunctionTypeDeclaration(declaration, owner)
            is UnionTypeDeclaration -> lowerUnionTypeDeclaration(declaration, owner)
            is TupleDeclaration -> lowerTupleDeclaration(declaration, owner)
            is IntersectionTypeDeclaration -> lowerIntersectionTypeDeclaration(declaration, owner)
            is ObjectLiteralDeclaration -> lowerObjectLiteralDeclaration(declaration, owner)
            else -> declaration
        }
    }
}