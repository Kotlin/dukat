package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.Declaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.FunctionOwnerDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
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
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

interface DeclarationLowering {
    fun lowerVariableDeclaration(declaration: VariableDeclaration, owner: NodeOwner<ModuleDeclaration>): VariableDeclaration
    fun lowerFunctionDeclaration(declaration: FunctionDeclaration, owner: NodeOwner<FunctionOwnerDeclaration>): FunctionDeclaration
    fun lowerClassDeclaration(declaration: ClassDeclaration, owner: NodeOwner<ModuleDeclaration>): ClassDeclaration
    fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration, owner: NodeOwner<ModuleDeclaration>): InterfaceDeclaration
    fun lowerGeneratedInterfaceDeclaration(declaration: GeneratedInterfaceDeclaration, owner: NodeOwner<ModuleDeclaration>): GeneratedInterfaceDeclaration
    fun lowerTypeDeclaration(declaration: TypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>): TypeDeclaration
    fun lowerFunctionTypeDeclaration(declaration: FunctionTypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>): FunctionTypeDeclaration
    fun lowerParameterDeclaration(declaration: ParameterDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>): ParameterDeclaration
    fun lowerTypeParameter(declaration: TypeParameterDeclaration, owner: NodeOwner<Declaration>): TypeParameterDeclaration
    fun lowerUnionTypeDeclaration(declaration: UnionTypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>): UnionTypeDeclaration
    fun lowerTupleDeclaration(declaration: TupleDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>): TupleDeclaration
    fun lowerIntersectionTypeDeclaration(declaration: IntersectionTypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>): IntersectionTypeDeclaration
    fun lowerMemberDeclaration(declaration: MemberDeclaration, owner: NodeOwner<ClassLikeDeclaration>): MemberDeclaration
    fun lowerMethodSignatureDeclaration(declaration: MethodSignatureDeclaration, owner: NodeOwner<MemberDeclaration>): MethodSignatureDeclaration
    fun lowerTypeAliasDeclaration(declaration: TypeAliasDeclaration, owner: NodeOwner<ModuleDeclaration>): TypeAliasDeclaration
    fun lowerIndexSignatureDeclaration(declaration: IndexSignatureDeclaration, owner: NodeOwner<MemberDeclaration>): IndexSignatureDeclaration

    fun lowerParameterValue(declaration: ParameterValueDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>): ParameterValueDeclaration {
        return when (declaration) {
            is TypeDeclaration -> lowerTypeDeclaration(declaration, owner)
            is FunctionTypeDeclaration -> lowerFunctionTypeDeclaration(declaration, owner)
            is UnionTypeDeclaration -> lowerUnionTypeDeclaration(declaration, owner)
            is TupleDeclaration -> lowerTupleDeclaration(declaration, owner)
            is IntersectionTypeDeclaration -> lowerIntersectionTypeDeclaration(declaration, owner)
            else -> declaration
        }
    }

    fun lowerClassLikeDeclaration(declaration: ClassLikeDeclaration, owner: NodeOwner<ModuleDeclaration>): ClassLikeDeclaration {
        return when (declaration) {
            is InterfaceDeclaration -> lowerInterfaceDeclaration(declaration, owner)
            is ClassDeclaration -> lowerClassDeclaration(declaration, owner)
            is GeneratedInterfaceDeclaration -> lowerGeneratedInterfaceDeclaration(declaration, owner)
            else -> declaration
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun lowerTopLevelDeclaration(declaration: TopLevelDeclaration, owner: NodeOwner<ModuleDeclaration>): TopLevelDeclaration {
        return when (declaration) {
            is VariableDeclaration -> lowerVariableDeclaration(declaration, owner)
            is FunctionDeclaration -> lowerFunctionDeclaration(declaration, owner as NodeOwner<FunctionOwnerDeclaration>)
            is ClassLikeDeclaration -> lowerClassLikeDeclaration(declaration, owner)
            is ModuleDeclaration -> lowerDocumentRoot(declaration, owner)
            is TypeAliasDeclaration -> lowerTypeAliasDeclaration(declaration, owner)
            else -> declaration
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<TopLevelDeclaration>, owner: NodeOwner<ModuleDeclaration>): List<TopLevelDeclaration> {
        return declarations.map { declaration ->
            lowerTopLevelDeclaration(declaration, owner)
        }
    }

    fun lowerDocumentRoot(documentRoot: ModuleDeclaration, owner: NodeOwner<ModuleDeclaration> = NodeOwner(documentRoot, null)): ModuleDeclaration {
        return documentRoot.copy(declarations = lowerTopLevelDeclarations(documentRoot.declarations, NodeOwner(documentRoot, null)))
    }

}