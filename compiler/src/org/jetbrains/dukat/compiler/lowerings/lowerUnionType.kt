package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.DynamicTypeNode
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration


private class LoweringUnionType : ParameterValueLowering {
    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        if (declaration is UnionTypeDeclaration) {
            return DynamicTypeNode(declaration)
        }
        return declaration
    }
}

fun DocumentRootDeclaration.lowerUnionType(): DocumentRootDeclaration {
    return LoweringUnionType().lowerDocumentRoot(this)
}