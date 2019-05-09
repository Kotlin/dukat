package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration


private class LoweringUnionType() : ParameterValueLowering {

    override fun lowerType(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        if (declaration is UnionTypeDeclaration) {
            return UnionTypeNode(
                    params = declaration.params.map { param -> lowerType(param) },
                    nullable = declaration.nullable
            )
        }
        return super.lowerType(declaration)
    }
}

fun DocumentRootNode.lowerUnionType(): DocumentRootNode {
    return org.jetbrains.dukat.nodeIntroduction.LoweringUnionType().lowerDocumentRoot(this)
}

fun SourceSetNode.lowerUnionType() = transform { it.lowerUnionType() }

