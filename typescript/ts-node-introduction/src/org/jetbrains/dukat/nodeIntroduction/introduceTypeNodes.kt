package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.TypeParameterNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.GeneratedInterfaceReferenceNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TupleTypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.convertToNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeParamReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

private class TypeNodesLowering : ParameterValueLowering {

    override fun lowerType(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is TypeParamReferenceDeclaration -> TypeParameterNode(
                    name = declaration.value,
                    nullable = declaration.nullable
            )
            is TypeDeclaration -> TypeValueNode(
                    value = declaration.value,
                    params = declaration.params.map { param -> lowerType(param) },
                    typeReference = declaration.typeReference,
                    nullable = declaration.nullable,
                    meta = declaration.meta
            )
            is FunctionTypeDeclaration -> FunctionTypeNode(
                    parameters = declaration.parameters.map { parameterDeclaration ->
                        parameterDeclaration.copy(type = lowerType(parameterDeclaration.type)).convertToNode()
                    },
                    type = lowerType(declaration.type),
                    nullable = declaration.nullable,
                    meta = declaration.meta
            )
            is GeneratedInterfaceReferenceDeclaration -> GeneratedInterfaceReferenceNode(
                    declaration.name,
                    declaration.typeParameters,
                    declaration.reference,
                    declaration.nullable,
                    declaration.meta
            )
            is UnionTypeDeclaration -> UnionTypeNode(
                    params = declaration.params.map { param -> lowerType(param) },
                    nullable = declaration.nullable,
                    meta = declaration.meta
            )
            is TupleDeclaration -> TupleTypeNode(
                    params = declaration.params.map { param -> lowerType(param) },
                    nullable = declaration.nullable,
                    meta = declaration.meta
            )
            else -> super.lowerType(declaration)
        }
    }
}

fun DocumentRootNode.introduceTypeNodes(): DocumentRootNode {
    return TypeNodesLowering().lowerDocumentRoot(this)
}

fun SourceSetNode.introduceTypeNodes() = transform { it.introduceTypeNodes() }