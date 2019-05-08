package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.ValueTypeNode
import org.jetbrains.dukat.ast.model.nodes.convertToNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetrbains.dukat.nodeLowering.ParameterValueLowering

private class TypeNodesLowering() : ParameterValueLowering {

    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is TypeDeclaration -> ValueTypeNode(
                    value = IdentifierNode(declaration.value),
                    params = declaration.params.map { param -> lowerParameterValue(param) },
                    nullable = declaration.nullable,
                    meta = declaration.meta
            )
            is FunctionTypeDeclaration -> FunctionTypeNode(
                    parameters = declaration.parameters.map { parameterDeclaration ->
                        parameterDeclaration.copy(type = lowerParameterValue(parameterDeclaration.type)).convertToNode()
                    },
                    type = lowerParameterValue(declaration.type),
                    nullable = declaration.nullable,
                    meta = declaration.meta
            )
            else -> super.lowerParameterValue(declaration)
        }
    }
}

fun DocumentRootNode.introduceTypeNodes(): DocumentRootNode {
    return org.jetbrains.dukat.nodeIntroduction.TypeNodesLowering().lowerDocumentRoot(this)
}

fun SourceSetNode.introduceTypeNodes() = transform { it.introduceTypeNodes() }