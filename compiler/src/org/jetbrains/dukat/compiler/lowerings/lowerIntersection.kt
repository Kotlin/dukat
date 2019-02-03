package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.compiler.asIntersection
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private class LowerIntersection : ParameterValueLowering {
    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        declaration.asIntersection()?.let {
            val firstIntersectionType = it.params[0]
            firstIntersectionType.meta = it.copy()
            return lowerParameterValue(firstIntersectionType)
        }

        return super.lowerParameterValue(declaration)
    }
}

fun DocumentRootNode.lowerIntersectionType(): DocumentRootNode {
    return LowerIntersection().lowerDocumentRoot(this)
}