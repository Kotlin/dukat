package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.DynamicTypeNode
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration


private class LoweringUnionType() : ParameterValueLowering {

    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        if (declaration is UnionTypeDeclaration) {
            return DynamicTypeNode(declaration.copy(params = declaration.params.map {param ->  lowerParameterValue(param)}))
        }
        return super.lowerParameterValue(declaration)
    }
}

fun DocumentRootNode.lowerUnionType(): DocumentRootNode {
    return LoweringUnionType().lowerDocumentRoot(this)
}