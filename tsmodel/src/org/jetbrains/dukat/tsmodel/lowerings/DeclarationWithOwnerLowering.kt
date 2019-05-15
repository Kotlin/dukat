package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.PackageDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
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

    fun lowerParameterValue(owner: NodeOwner<ParameterValueDeclaration>): ParameterValueDeclaration {
        val declaration = owner.node
        return when (declaration) {
            is TypeDeclaration -> lowerTypeDeclaration(owner.wrap(declaration))
            is FunctionTypeDeclaration -> lowerFunctionTypeDeclaration(owner.wrap(declaration))
            is UnionTypeDeclaration -> lowerUnionTypeDeclation(owner.wrap(declaration))
            is IntersectionTypeDeclaration -> lowerIntersectionTypeDeclatation(owner.wrap(declaration))
            else -> declaration
        }
    }


    fun lowerClassLikeDeclaration(owner: NodeOwner<ClassLikeDeclaration>): ClassLikeDeclaration {
        val declaration = owner.node
        return when (declaration) {
            is InterfaceDeclaration -> lowerInterfaceDeclaration(owner.wrap(declaration))
            is ClassDeclaration -> lowerClassDeclaration(owner.wrap(declaration))
            else -> declaration
        }
    }

    fun lowerTopLevelDeclaration(owner: NodeOwner<TopLevelEntity>): TopLevelEntity {
        val declaration = owner.node
        return when (declaration) {
            is VariableDeclaration -> lowerVariableDeclaration(owner.wrap(declaration))
            is FunctionDeclaration -> lowerFunctionDeclaration(owner.wrap(declaration))
            is ClassLikeDeclaration -> lowerClassLikeDeclaration(owner.wrap(declaration))
            is PackageDeclaration -> lowerDocumentRoot(declaration, owner.wrap(declaration))
            is TypeAliasDeclaration -> lowerTypeAliasDeclaration(owner.wrap(declaration))
            else -> declaration
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<TopLevelEntity>, owner: NodeOwner<PackageDeclaration>): List<TopLevelEntity> {
        return declarations.map { declaration ->
            lowerTopLevelDeclaration(owner.wrap(declaration))
        }
    }

    fun lowerDocumentRoot(documentRoot: PackageDeclaration, owner: NodeOwner<PackageDeclaration>): PackageDeclaration {
        return documentRoot.copy(declarations = lowerTopLevelDeclarations(documentRoot.declarations, owner))
    }

}