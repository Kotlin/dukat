package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

interface DeclarationWithOwnerLowering {
    fun lowerVariableDeclaration(owner: NodeOwner<VariableDeclaration>): VariableDeclaration
    fun lowerFunctionDeclaration(owner: NodeOwner<FunctionDeclaration>): FunctionDeclaration
    fun lowerClassDeclaration(owner: NodeOwner<ClassDeclaration>): ClassDeclaration
    fun lowerInterfaceDeclaration(owner: NodeOwner<InterfaceDeclaration>): InterfaceDeclaration
    fun lowerTypeDeclaration(owner: NodeOwner<TypeDeclaration>): TypeDeclaration
    fun lowerFunctionTypeDeclaration(owner: NodeOwner<FunctionTypeDeclaration>): FunctionTypeDeclaration
    fun lowerParameterDeclaration(owner: NodeOwner<ParameterDeclaration>): ParameterDeclaration
    fun lowerTypeParameter(owner: NodeOwner<TypeParameterDeclaration>): TypeParameterDeclaration
    fun lowerUnionTypeDeclation(owner: NodeOwner<UnionTypeDeclaration>): UnionTypeDeclaration
    fun lowerIntersectionTypeDeclatation(owner: NodeOwner<IntersectionTypeDeclaration>): IntersectionTypeDeclaration
    fun lowerMemberDeclaration(owner: NodeOwner<MemberEntity>): MemberEntity
    fun lowerMethodSignatureDeclaration(owner: NodeOwner<MethodSignatureDeclaration>): MethodSignatureDeclaration
    fun lowerTypeAliasDeclaration(owner: NodeOwner<TypeAliasDeclaration>): TypeAliasDeclaration
    fun lowerObjectDeclaration(owner: NodeOwner<ObjectLiteralDeclaration>): ParameterValueDeclaration
    fun lowerTupleDeclaration(owner: NodeOwner<TupleDeclaration>): TupleDeclaration

    fun lowerParameterValue(owner: NodeOwner<ParameterValueDeclaration>): ParameterValueDeclaration {
        val declaration = owner.node
        return when (declaration) {
            is TypeDeclaration -> lowerTypeDeclaration(owner as NodeOwner<TypeDeclaration>)
            is FunctionTypeDeclaration -> lowerFunctionTypeDeclaration(owner as NodeOwner<FunctionTypeDeclaration>)
            is UnionTypeDeclaration -> lowerUnionTypeDeclation(owner as NodeOwner<UnionTypeDeclaration>)
            is IntersectionTypeDeclaration -> lowerIntersectionTypeDeclatation(owner as NodeOwner<IntersectionTypeDeclaration>)
            is ObjectLiteralDeclaration -> lowerObjectDeclaration(owner as NodeOwner<ObjectLiteralDeclaration>)
            is TupleDeclaration -> lowerTupleDeclaration(owner as NodeOwner<TupleDeclaration>)
            else -> declaration
        }
    }


    fun lowerClassLikeDeclaration(owner: NodeOwner<ClassLikeDeclaration>): ClassLikeDeclaration {
        val declaration = owner.node
        return when (declaration) {
            is InterfaceDeclaration -> lowerInterfaceDeclaration(owner as NodeOwner<InterfaceDeclaration>)
            is ClassDeclaration -> lowerClassDeclaration(owner as NodeOwner<ClassDeclaration>)
            else -> declaration
        }
    }

    fun lowerTopLevelDeclaration(owner: NodeOwner<TopLevelEntity>): TopLevelEntity {
        val declaration = owner.node
        return when (declaration) {
            is VariableDeclaration -> lowerVariableDeclaration(owner as NodeOwner<VariableDeclaration>)
            is FunctionDeclaration -> lowerFunctionDeclaration(owner as NodeOwner<FunctionDeclaration>)
            is ClassLikeDeclaration -> lowerClassLikeDeclaration(owner as NodeOwner<ClassLikeDeclaration>)
            is ModuleDeclaration -> lowerDocumentRoot(declaration, owner as NodeOwner<ModuleDeclaration>)
            is TypeAliasDeclaration -> lowerTypeAliasDeclaration(owner as NodeOwner<TypeAliasDeclaration>)
            else -> declaration
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<TopLevelEntity>, owner: NodeOwner<ModuleDeclaration>): List<TopLevelEntity> {
        return declarations.map { declaration ->
            lowerTopLevelDeclaration(owner.wrap(declaration))
        }
    }

    fun lowerDocumentRoot(documentRoot: ModuleDeclaration, owner: NodeOwner<ModuleDeclaration>): ModuleDeclaration {
        return documentRoot.copy(declarations = lowerTopLevelDeclarations(documentRoot.declarations, owner))
    }

}