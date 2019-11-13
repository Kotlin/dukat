package org.jetrbains.dukat.nodeLowering

import org.jetbrains.dukat.ast.model.TypeParameterNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.TupleTypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.metadata.IntersectionMetadata
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration


interface NodeTypeLowering : TypeLowering {

    fun lowerTupleNode(declaration: TupleTypeNode): TupleTypeNode {
        return declaration.copy(params = declaration.params.map { param -> lowerType(param) })
    }

    fun lowerTypeParameter(declaration: TypeParameterNode): TypeParameterNode {
        return declaration.copy(meta = declaration.meta?.let {
            lowerType(it)
        })
    }

    override fun lowerType(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is TypeParameterNode -> lowerTypeParameter(declaration)
            is TypeValueNode -> lowerTypeNode(declaration)
            is FunctionTypeNode -> lowerFunctionNode(declaration)
            is UnionTypeNode -> lowerUnionTypeNode(declaration)
            is TupleTypeNode -> lowerTupleNode(declaration)
            else -> declaration
        }
    }

}