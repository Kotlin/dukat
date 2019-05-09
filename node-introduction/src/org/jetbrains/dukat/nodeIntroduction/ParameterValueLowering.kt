package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration
import org.jetrbains.dukat.nodeLowering.TypeLowering


internal interface ParameterValueLowering : TypeLowering {

    override fun lowerType(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is TypeValueNode -> lowerTypeNode(declaration)
            is FunctionTypeNode -> lowerFunctionNode(declaration)
            is UnionTypeDeclaration -> lowerUnionTypeDeclaration(declaration)
            is IntersectionTypeDeclaration -> lowerIntersectionTypeDeclaration(declaration)
            is UnionTypeNode -> lowerUnionTypeNode(declaration)
            is TupleDeclaration -> lowerTupleDeclaration(declaration)
            else -> declaration
        }
    }

}