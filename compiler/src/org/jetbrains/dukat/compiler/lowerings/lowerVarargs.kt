package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.ValueTypeNode
import org.jetbrains.dukat.ast.model.nodes.isPrimitive
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private fun ParameterValueDeclaration.extractVarargType(): ParameterValueDeclaration {
    if (this is ValueTypeNode) {
        when  {
            isPrimitive("Array") -> return params[0]
            isPrimitive("Any") -> return this
        }
    }

    return this
}

private class LoweringVarags : ParameterValueLowering {
    override fun lowerParameterNode(declaration: ParameterNode): ParameterNode {
        return if (declaration.vararg) declaration.copy(type = declaration.type.extractVarargType()) else declaration
    }
}

fun DocumentRootNode.lowerVarargs(): DocumentRootNode {
    return LoweringVarags().lowerDocumentRoot(this)
}

fun SourceSetNode.lowerVarargs() = transform { it.lowerVarargs() }