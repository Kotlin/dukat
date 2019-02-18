package org.jetbrains.dukat.compiler.lowerings.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.compiler.lowerings.ParameterValueLowering
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private class TypeNodesLowering() : ParameterValueLowering {
    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when(declaration) {
            is TypeDeclaration -> TypeNode(
                    value = IdentifierNode(declaration.value),
                    params = declaration.params.map { param -> lowerParameterValue(param) },
                    nullable = declaration.nullable,
                    meta = declaration.meta
            )
            is FunctionTypeDeclaration -> FunctionTypeNode(
                parameters = declaration.parameters.map { parameterDeclaration ->
                    parameterDeclaration.copy(type = lowerParameterValue(parameterDeclaration.type))
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
    return TypeNodesLowering().lowerDocumentRoot(this)
}