package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.convertToNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeParamReferenceDeclaration

private class TypeNodesLowering() : ParameterValueLowering {

    override fun lowerType(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is TypeParamReferenceDeclaration -> TypeValueNode(
                    value = declaration.value,
                    params = emptyList(),
                    typeReference = null,
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
            else -> super.lowerType(declaration)
        }
    }
}

fun DocumentRootNode.introduceTypeNodes(): DocumentRootNode {
    return TypeNodesLowering().lowerDocumentRoot(this)
}

fun SourceSetNode.introduceTypeNodes() = transform { it.introduceTypeNodes() }