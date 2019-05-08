package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.metadata.IntersectionMetadata
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetrbains.dukat.nodeLowering.ParameterValueLowering
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private class LowerIntersection : ParameterValueLowering {
    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is IntersectionTypeDeclaration -> {
                val firstIntersectionType = declaration.params[0].duplicate<ParameterValueDeclaration>()
                firstIntersectionType.meta =
                        IntersectionMetadata(declaration.params)
                return lowerParameterValue(firstIntersectionType)
            }
            else -> declaration
        }
    }
}

fun DocumentRootNode.lowerIntersectionType(): DocumentRootNode {
    return LowerIntersection().lowerDocumentRoot(this)
}

fun SourceSetNode.lowerIntersectionType() = transform { it.lowerIntersectionType() }