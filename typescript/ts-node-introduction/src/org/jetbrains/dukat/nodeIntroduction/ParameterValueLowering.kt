package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.TupleTypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration
import org.jetrbains.dukat.nodeLowering.TypeLowering


internal interface ParameterValueLowering : TypeLowering {

    fun lowerUnionTypeDeclaration(declaration: UnionTypeDeclaration): UnionTypeDeclaration {
        return declaration.copy(params = declaration.params.map { param -> lowerType(param) })
    }

    fun lowerIntersectionTypeDeclaration(declaration: IntersectionTypeDeclaration): IntersectionTypeDeclaration {
        return declaration.copy(params = declaration.params.map { param -> lowerType(param) })
    }

    fun lowerTupleDeclaration(declaration: TupleDeclaration): ParameterValueDeclaration {
        return declaration.copy(params = declaration.params.map { param -> lowerType(param) })
    }

    fun lowerTupleTypeNode(declaration: TupleTypeNode): ParameterValueDeclaration {
        return declaration.copy(params = declaration.params.map { param -> lowerType(param) })
    }

    override fun lowerType(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is UnionTypeDeclaration -> lowerUnionTypeDeclaration(declaration)
            is TupleDeclaration -> lowerTupleDeclaration(declaration)
            is IntersectionTypeDeclaration -> lowerIntersectionTypeDeclaration(declaration)

            is TypeValueNode -> lowerTypeNode(declaration)
            is FunctionTypeNode -> lowerFunctionNode(declaration)
            is UnionTypeNode -> lowerUnionTypeNode(declaration)
            is TupleTypeNode -> lowerTupleTypeNode(declaration)
            else -> declaration
        }
    }

}