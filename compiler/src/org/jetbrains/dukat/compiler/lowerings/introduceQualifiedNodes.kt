package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.tsmodel.QualifiedNamedDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private class LowerQualifiedDeclarations : ParameterValueLowering {
    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is QualifiedNamedDeclaration -> QualifiedNode(declaration.left, declaration.right)
            else -> declaration
        }
    }
}

fun DocumentRootNode.introduceQualifiedNode() : DocumentRootNode {
    return LowerQualifiedDeclarations().lowerDocumentRoot(this)
}