package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ExportQualifier
import org.jetbrains.dukat.tsmodel.JsModule
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetrbains.dukat.nodeLowering.NodeWithOwnerTypeLowering
import org.jetrbains.dukat.nodeLowering.lowerings.NodeLowering

private class ResolveQualifierAnnotations(
        private val exportQualifierMap: MutableMap<String?, ExportQualifier>
) : NodeWithOwnerTypeLowering {

    override fun lowerRoot(moduleNode: ModuleNode, owner: NodeOwner<ModuleNode>): ModuleNode {
        val nodeResolved = exportQualifierMap[moduleNode.uid]?.let { exportQualifier ->
            when (exportQualifier) {
                is JsModule -> moduleNode.copy(
                        jsModule = exportQualifier.name,
                        jsQualifier = if (exportQualifier.qualifier) moduleNode.qualifiedPackageName else null
                )
                else -> null
            }
        } ?: moduleNode

        return super.lowerRoot(nodeResolved, owner)
    }

    override fun lowerFunctionNode(owner: NodeOwner<FunctionNode>): FunctionNode {
        val nodeResolved = exportQualifierMap[owner.node.uid]?.let { exportQualifier ->
            when (exportQualifier) {
                is JsModule -> owner.node.copy(exportQualifier = exportQualifier)
                else -> null
            }
        } ?: owner.node

        return super.lowerFunctionNode(owner.copy(node = nodeResolved))
    }

    override fun lowerClassNode(owner: NodeOwner<ClassNode>): ClassNode {
        val nodeResolved = exportQualifierMap[owner.node.uid]?.let { exportQualifier ->
            when (exportQualifier) {
                is JsModule -> owner.node.copy(exportQualifier = exportQualifier)
                else -> null
            }
        } ?: owner.node


        return super.lowerClassNode(owner.copy(node = nodeResolved))
    }

    override fun lowerVariableNode(owner: NodeOwner<VariableNode>): VariableNode {
        val nodeResolved = exportQualifierMap[owner.node.uid]?.let { exportQualifier ->
            when (exportQualifier) {
                is JsModule -> owner.node.copy(
                        exportQualifier = exportQualifier,
                        immutable = true
                )
                else -> null
            }
        } ?: owner.node

        return super.lowerVariableNode(owner.copy(node = nodeResolved))
    }
}

class ResolveModuleAnnotations(private val exportQualifierMap: MutableMap<String?, ExportQualifier>) : NodeLowering {
    override fun lower(source: SourceSetNode): SourceSetNode {
        return source.copy(sources = source.sources.map { sourceFileNode ->
            sourceFileNode.copy(root = ResolveQualifierAnnotations(exportQualifierMap).lowerRoot(sourceFileNode.root, NodeOwner(sourceFileNode.root, null)))
        })
    }
}