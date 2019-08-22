package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TupleTypeNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration

private class TupleNodes : ParameterValueLowering {

    override fun lowerType(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        if (declaration is TupleDeclaration) {
            return TupleTypeNode(
                params = declaration.params.map { param -> lowerType(param) },
                nullable = declaration.nullable
            )
        }
        return super.lowerType(declaration)
    }
}

fun DocumentRootNode.introduceTupleNodes(): DocumentRootNode {
    return TupleNodes().lowerDocumentRoot(this)
}

fun SourceSetNode.introduceTupleNodes() = transform { it.introduceTupleNodes() }

