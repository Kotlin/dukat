package org.jetrbains.dukat.nodeLowering.lowerings

import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.isPrimitive
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetrbains.dukat.nodeLowering.NodeTypeLowering

private fun ParameterValueDeclaration.extractVarargType(): ParameterValueDeclaration {
    if (this is TypeValueNode) {
        when {
            isPrimitive("Array") -> return params[0]
            isPrimitive("Any") -> return this
        }
    }

    return this
}

private class LoweringVarags : NodeTypeLowering {
    override fun lowerParameterNode(declaration: ParameterNode): ParameterNode {
        return if (declaration.vararg) declaration.copy(type = declaration.type.extractVarargType()) else declaration
    }
}

private fun ModuleNode.lowerVarargs(): ModuleNode {
    return LoweringVarags().lowerModuleNode(this)
}

private fun SourceSetNode.lowerVarargs() = transform { it.lowerVarargs() }

class LowerVarargs(): NodeLowering {
    override fun lower(source: SourceSetNode): SourceSetNode {
        return source.lowerVarargs()
    }
}