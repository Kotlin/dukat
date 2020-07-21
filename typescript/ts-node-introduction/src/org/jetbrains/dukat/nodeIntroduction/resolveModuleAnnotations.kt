package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.export.ExportQualifier
import org.jetbrains.dukat.ast.model.nodes.export.JsDefault
import org.jetbrains.dukat.ast.model.nodes.export.JsModule
import org.jetbrains.dukat.astCommon.isStringLiteral
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetrbains.dukat.nodeLowering.NodeWithOwnerTypeLowering
import org.jetrbains.dukat.nodeLowering.lowerings.NodeLowering


private fun buildExportAssignmentTable(docRoot: ModuleNode, assignExports: MutableMap<String, ExportQualifier> = mutableMapOf()): Map<String, ExportQualifier> {
    val exports = docRoot.export
    if (exports?.isExportEquals == true) {
        exports.uids.forEach { uid ->
            docRoot.moduleName?.let { assignExports[uid] = JsModule(it) }
        }
    } else {
        exports?.uids?.forEach { uid ->
            docRoot.moduleName?.let { assignExports[uid] = JsDefault }
        }
    }

    docRoot.declarations.forEach { declaration ->
        if (declaration is ModuleNode) {
            buildExportAssignmentTable(declaration, assignExports)
        }
    }

    return assignExports
}

private fun NodeOwner<*>.moduleOwner(): ModuleNode? {
    return generateSequence(owner) { it.owner }.firstOrNull { it.node is ModuleNode }?.node as? ModuleNode
}

private class ExportAssignmentLowering(
        private val root: ModuleNode,
        private val exportQualifierMap: MutableMap<String, ExportQualifier>
) : NodeWithOwnerTypeLowering {
    private val assignExports: Map<String, ExportQualifier> = buildExportAssignmentTable(root)

    override fun lowerRoot(moduleNode: ModuleNode, owner: NodeOwner<ModuleNode>): ModuleNode {
        if (assignExports.contains(moduleNode.uid)) {
            (assignExports[moduleNode.uid] as? JsModule)?.let { jsModule ->
                exportQualifierMap[moduleNode.uid] = JsModule(
                        name = jsModule.name,
                        qualifier = null
                )
            }
        } else {
            if (moduleNode.uid != root.uid) {
                exportQualifierMap[moduleNode.uid] = if (moduleNode.packageName.isStringLiteral()) {
                    JsModule(
                            name = moduleNode.packageName,
                            qualifier = null
                    )
                } else {
                    JsModule(
                            name = null,
                            qualifier = moduleNode.qualifiedPackageName
                    )
                }
            }
        }

        return super.lowerRoot(moduleNode, owner)
    }

    override fun lowerFunctionNode(owner: NodeOwner<FunctionNode>): FunctionNode {
        val declaration = owner.node
        val exportQualifier = assignExports[declaration.uid]

        when (exportQualifier) {
            is JsModule -> {
                exportQualifierMap[declaration.uid] = JsModule(exportQualifier.name, null)

                val moduleNode = owner.moduleOwner()

                if (moduleNode != null) {
                    exportQualifierMap[moduleNode.uid] = JsModule(null, null)
                }
            }
            is JsDefault -> {
                val moduleNode = owner.moduleOwner()
                if (moduleNode != null) {
                    exportQualifierMap[moduleNode.uid] = JsModule(moduleNode.moduleName, null)
                }
            }
        }


        return declaration
    }

    override fun lowerClassNode(owner: NodeOwner<ClassNode>): ClassNode {
        val declaration = owner.node
        val exportQualifier = assignExports[declaration.uid]

        when (exportQualifier) {
            is JsModule -> {
                exportQualifierMap[declaration.uid] = JsModule(exportQualifier.name, null)

                val moduleNode = owner.moduleOwner()

                if (moduleNode != null) {
                    exportQualifierMap[moduleNode.uid] = JsModule(null, null)
                }
            }
            is JsDefault -> {
                val moduleNode = owner.moduleOwner()
                if (moduleNode != null) {
                    exportQualifierMap[moduleNode.uid] = JsModule(moduleNode.moduleName, null)
                }
            }
        }

        return declaration
    }

    override fun lowerVariableNode(owner: NodeOwner<VariableNode>): VariableNode {
        val declaration = owner.node
        val exportQualifier = assignExports[declaration.uid]

        when (exportQualifier) {
            is JsModule -> {
                exportQualifierMap[declaration.uid] = JsModule(exportQualifier.name, null)

                val moduleNode = owner.moduleOwner()

                if (moduleNode != null) {
                    exportQualifierMap[moduleNode.uid] = JsModule(null, null)
                }

                if (moduleNode?.packageName?.isStringLiteral() == true) {
                    exportQualifier.name?.let { declaration.name = it }
                }
            }
            is JsDefault -> {
                val moduleNode = owner.moduleOwner()
                if (moduleNode != null) {
                    exportQualifierMap[moduleNode.uid] = JsModule(moduleNode.moduleName, null)
                }
            }
        }

        return declaration
    }
}

private fun ModuleNode.resolveModuleAnnotations(exportQualifierMap: MutableMap<String, ExportQualifier>): ModuleNode {
    return ExportAssignmentLowering(this, exportQualifierMap).lowerRoot(this, NodeOwner(this, null))
}

private fun SourceSetNode.resolveModuleAnnotations(exportQualifierMap: MutableMap<String, ExportQualifier>): SourceSetNode {
    return copy(sources = sources.map { sourceFileNode ->
        sourceFileNode.copy(root = sourceFileNode.root.resolveModuleAnnotations(exportQualifierMap))
    })
}

private class ResolveQualifierAnnotations(
        private val exportQualifierMap: MutableMap<String, ExportQualifier>
) : NodeWithOwnerTypeLowering {

    override fun lowerRoot(moduleNode: ModuleNode, owner: NodeOwner<ModuleNode>): ModuleNode {
        val nodeResolved = exportQualifierMap[moduleNode.uid]?.let { exportQualifier ->
            when (exportQualifier) {
                is JsModule -> moduleNode.copy(
                        jsModule = exportQualifier.name,
                        jsQualifier = exportQualifier.qualifier
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

class ResolveModuleAnnotations : NodeLowering {
    override fun lower(source: SourceSetNode): SourceSetNode {
        val exportQualifierMap = mutableMapOf<String, ExportQualifier>()
        val sourceSet = source.resolveModuleAnnotations(exportQualifierMap)
        return sourceSet.copy(sources = sourceSet.sources.map { sourceFileNode ->
            sourceFileNode.copy(root = ResolveQualifierAnnotations(exportQualifierMap).lowerRoot(sourceFileNode.root, NodeOwner(sourceFileNode.root, null)))
        })
    }
}