package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.makeNullable
import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.metadata.IntersectionMetadata
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetrbains.dukat.nodeLowering.lowerings.NodeLowering

private class LowerIntersection : ParameterValueLowering {
    override fun lowerType(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is IntersectionTypeDeclaration -> {
                val duplicated =
                    declaration.params[0].duplicate<ParameterValueDeclaration>()
                val firstIntersectionType = if (declaration.nullable) duplicated.makeNullable() else duplicated
                firstIntersectionType.meta =
                        IntersectionMetadata(declaration.params)

                return lowerType(firstIntersectionType)
            }
            else -> super.lowerType(declaration)
        }
    }
}

private fun ModuleNode.lowerIntersectionType(): ModuleNode {
    return LowerIntersection().lowerModuleNode(this)
}

private fun SourceSetNode.lowerIntersectionType() = transform { it.lowerIntersectionType() }

class LowerIntersectionType(): NodeLowering {
    override fun lower(source: SourceSetNode): SourceSetNode {
        return source.lowerIntersectionType()
    }
}