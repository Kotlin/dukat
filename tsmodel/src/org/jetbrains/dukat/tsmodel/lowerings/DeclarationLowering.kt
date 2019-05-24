package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.PackageDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
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
    fun lowerVariableDeclaration(declaration: VariableDeclaration): VariableDeclaration
    fun lowerFunctionDeclaration(declaration: FunctionDeclaration): FunctionDeclaration
    fun lowerClassDeclaration(declaration: ClassDeclaration): ClassDeclaration
    fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration): InterfaceDeclaration
    fun lowerGeneratedInterfaceDeclaration(declaration: GeneratedInterfaceDeclaration): GeneratedInterfaceDeclaration
    fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration
    fun lowerFunctionTypeDeclaration(declaration: FunctionTypeDeclaration): FunctionTypeDeclaration
    fun lowerParameterDeclaration(declaration: ParameterDeclaration): ParameterDeclaration
    fun lowerTypeParameter(declaration: TypeParameterDeclaration): TypeParameterDeclaration
    fun lowerUnionTypeDeclaration(declaration: UnionTypeDeclaration): UnionTypeDeclaration
    fun lowerTupleDeclaration(declaration: TupleDeclaration): TupleDeclaration
    fun lowerIntersectionTypeDeclaration(declaration: IntersectionTypeDeclaration): IntersectionTypeDeclaration
    fun lowerMemberDeclaration(declaration: MemberEntity): MemberEntity
    fun lowerMethodSignatureDeclaration(declaration: MethodSignatureDeclaration): MethodSignatureDeclaration
    fun lowerTypeAliasDeclaration(declaration: TypeAliasDeclaration): TypeAliasDeclaration
    fun lowerIndexSignatureDeclaration(declaration: IndexSignatureDeclaration): IndexSignatureDeclaration

    fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is TypeDeclaration -> lowerTypeDeclaration(declaration)
            is FunctionTypeDeclaration -> lowerFunctionTypeDeclaration(declaration)
            is UnionTypeDeclaration -> lowerUnionTypeDeclaration(declaration)
            is TupleDeclaration -> lowerTupleDeclaration(declaration)
            is IntersectionTypeDeclaration -> lowerIntersectionTypeDeclaration(declaration)
            else -> declaration
        }
    }


    fun lowerClassLikeDeclaration(declaration: ClassLikeDeclaration): ClassLikeDeclaration {
        return when (declaration) {
            is InterfaceDeclaration -> lowerInterfaceDeclaration(declaration)
            is ClassDeclaration -> lowerClassDeclaration(declaration)
            is GeneratedInterfaceDeclaration -> lowerGeneratedInterfaceDeclaration(declaration)
            else -> declaration
        }
    }

    fun lowerTopLevelDeclaration(declaration: TopLevelEntity): TopLevelEntity {
        return when (declaration) {
            is VariableDeclaration -> lowerVariableDeclaration(declaration)
            is FunctionDeclaration -> lowerFunctionDeclaration(declaration)
            is ClassLikeDeclaration -> lowerClassLikeDeclaration(declaration)
            is PackageDeclaration -> lowerDocumentRoot(declaration)
            is TypeAliasDeclaration -> lowerTypeAliasDeclaration(declaration)
            else -> declaration
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<TopLevelEntity>): List<TopLevelEntity> {
        return declarations.map { declaration ->
            lowerTopLevelDeclaration(declaration)
        }
    }

    fun lowerDocumentRoot(documentRoot: PackageDeclaration): PackageDeclaration {
        return documentRoot.copy(declarations = lowerTopLevelDeclarations(documentRoot.declarations))
    }

}