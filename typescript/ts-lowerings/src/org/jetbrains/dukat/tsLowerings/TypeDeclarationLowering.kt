package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeParamReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

open class TypeDeclarationLowering(private val declarationLowering: DeclarationLowering)  {

    open fun lowerUnionTypeDeclaration(declaration: UnionTypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): UnionTypeDeclaration {
        return declaration.copy(params = declaration.params.map { param -> lower(param, owner?.wrap(declaration)) })
    }

    open fun lowerIntersectionTypeDeclaration(declaration: IntersectionTypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): IntersectionTypeDeclaration {
        return declaration.copy(params = declaration.params.map { param -> lower(param, owner?.wrap(declaration)) })
    }

    open fun lowerTupleDeclaration(declaration: TupleDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): TupleDeclaration {
        return declaration.copy(params = declaration.params.map { param -> lower(param, owner?.wrap(declaration)) })
    }

    open fun lowerTypeDeclaration(declaration: TypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): TypeDeclaration {
        return declaration.copy(params = declaration.params.map { param -> lower(param, owner?.wrap(declaration)) })
    }

    open fun lowerFunctionTypeDeclaration(declaration: FunctionTypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): FunctionTypeDeclaration {
        return declaration.copy(
                parameters = declaration.parameters.map { param -> this.declarationLowering.lowerParameterDeclaration(param, owner?.wrap(declaration)) },
                type = lower(declaration.type, owner?.wrap(declaration))
        )
    }
    open fun lowerTypeParamReferenceDeclaration(declaration: TypeParamReferenceDeclaration): ParameterValueDeclaration = declaration

    fun lower(declaration: ParameterValueDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): ParameterValueDeclaration {
        return when (declaration) {
            is TypeDeclaration -> lowerTypeDeclaration(declaration, owner)
            is FunctionTypeDeclaration -> lowerFunctionTypeDeclaration(declaration, owner)
            is UnionTypeDeclaration -> lowerUnionTypeDeclaration(declaration, owner)
            is TypeParamReferenceDeclaration -> lowerTypeParamReferenceDeclaration(declaration)
            is TupleDeclaration -> lowerTupleDeclaration(declaration, owner)
            is IntersectionTypeDeclaration -> lowerIntersectionTypeDeclaration(declaration, owner)
            is ObjectLiteralDeclaration -> this.declarationLowering.lowerObjectLiteralDeclaration(declaration, owner)
            else -> declaration
        }
    }
}