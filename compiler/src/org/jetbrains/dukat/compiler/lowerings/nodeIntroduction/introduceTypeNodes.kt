package org.jetbrains.dukat.compiler.lowerings.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.compiler.lowerings.ParameterValueLowering
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private class TypeNodesLowering() : ParameterValueLowering {
    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when(declaration) {
            is TypeDeclaration -> TypeNode(
                    value = declaration.value,
                    params = declaration.params.map { param -> lowerParameterValue(param) },
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