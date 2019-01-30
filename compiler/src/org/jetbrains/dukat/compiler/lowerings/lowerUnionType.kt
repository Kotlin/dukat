package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.UnionTypeDeclaration
import org.jetbrains.dukat.ast.model.nodes.DynamicTypeNode



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