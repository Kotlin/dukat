package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.export.ExportQualifier
import org.jetbrains.dukat.ast.model.nodes.export.JsDefault
import org.jetbrains.dukat.ast.model.nodes.export.JsModule
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.isStringLiteral
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetrbains.dukat.nodeLowering.NodeWithOwnerTypeLowering
import org.jetrbains.dukat.nodeLowering.lowerings.NodeLowering


private data class QualifierData(
        val jsModule: NameEntity?,
        val jsQualifier: NameEntity?
)

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
        private val qualifierDataMap: MutableMap<String, QualifierData>
) : NodeWithOwnerTypeLowering {
    private val assignExports: Map<String, ExportQualifier> = buildExportAssignmentTable(root)

    override fun lowerRoot(moduleNode: ModuleNode, owner: NodeOwner<ModuleNode>): ModuleNode {
        if (assignExports.contains(moduleNode.uid)) {
            (assignExports[moduleNode.uid] as? JsModule)?.let { jsModule ->
                qualifierDataMap[moduleNode.uid] = QualifierData(
                    jsModule = jsModule.name,
                    jsQualifier = null
                )
            }
        } else {
            if (moduleNode.uid != root.uid) {
                qualifierDataMap[moduleNode.uid] = if (moduleNode.packageName.isStringLiteral()) {
                    QualifierData(
                            jsModule = moduleNode.packageName,
                            jsQualifier = null
                    )
                } else {
                    QualifierData(
                            jsModule = null,
                            jsQualifier = moduleNode.qualifiedPackageName
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
                qualifierDataMap[declaration.uid] = QualifierData(null, exportQualifier.name)

                val moduleNode = owner.moduleOwner()

                if (moduleNode != null) {
                    qualifierDataMap[moduleNode.uid] = QualifierData(null, null)
                }
            }
            is JsDefault -> {
                val moduleNode = owner.moduleOwner()
                if (moduleNode != null) {
                    qualifierDataMap[moduleNode.uid] = QualifierData(moduleNode.moduleName, null)
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
                qualifierDataMap[declaration.uid] = QualifierData(null, exportQualifier.name)

                val moduleNode = owner.moduleOwner()

                if (moduleNode != null) {
                    qualifierDataMap[moduleNode.uid] = QualifierData(null, null)
                }
            }
            is JsDefault -> {
                val moduleNode = owner.moduleOwner()
                if (moduleNode != null) {
                    qualifierDataMap[moduleNode.uid] = QualifierData(moduleNode.moduleName, null)
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
                qualifierDataMap[declaration.uid] = QualifierData(null, exportQualifier.name)

                val moduleNode = owner.moduleOwner()

                if (moduleNode != null) {
                    qualifierDataMap[moduleNode.uid] = QualifierData(null, null)
                }

                if (moduleNode?.packageName?.isStringLiteral() == true) {
                    declaration.name = exportQualifier.name
                }
            }
            is JsDefault -> {
                val moduleNode = owner.moduleOwner()
                if (moduleNode != null) {
                    qualifierDataMap[moduleNode.uid] = QualifierData(moduleNode.moduleName, null)
                }
            }
        }

        return declaration
    }
}

private fun ModuleNode.resolveModuleAnnotations(qualifierDataMap: MutableMap<String, QualifierData>): ModuleNode {
    return ExportAssignmentLowering(this, qualifierDataMap).lowerRoot(this, NodeOwner(this, null))
}

private fun SourceSetNode.resolveModuleAnnotations(qualifierDataMap: MutableMap<String, QualifierData>): SourceSetNode {
    return copy(sources = sources.map { sourceFileNode ->
        sourceFileNode.copy(root = sourceFileNode.root.resolveModuleAnnotations(qualifierDataMap))
    })
}

private class ResolveQualifierAnnotations(
        private val qualifierDataMap: MutableMap<String, QualifierData>
) : NodeWithOwnerTypeLowering {

    override fun lowerRoot(moduleNode: ModuleNode, owner: NodeOwner<ModuleNode>): ModuleNode {
        val nodeResolved = qualifierDataMap[moduleNode.uid]?.let { qualifierData ->
            moduleNode.copy(
                jsModule =  qualifierData.jsModule,
                jsQualifier = qualifierData.jsQualifier
            )
         } ?: moduleNode

        return super.lowerRoot(nodeResolved, owner)
    }

    override fun lowerFunctionNode(owner: NodeOwner<FunctionNode>): FunctionNode {
        val nodeResolved = qualifierDataMap[owner.node.uid]?.let { qualifierData ->
            owner.node.copy(exportQualifier = qualifierData.jsQualifier?.let { JsModule(it)  })
        } ?: owner.node

        return super.lowerFunctionNode(owner.copy(node = nodeResolved))
    }

    override fun lowerClassNode(owner: NodeOwner<ClassNode>): ClassNode {
        val nodeResolved = qualifierDataMap[owner.node.uid]?.let { qualifierData ->
            owner.node.copy(exportQualifier = qualifierData.jsQualifier?.let { JsModule(it)  })
        } ?: owner.node

        return super.lowerClassNode(owner.copy(node = nodeResolved))
    }

    override fun lowerVariableNode(owner: NodeOwner<VariableNode>): VariableNode {
        val nodeResolved = qualifierDataMap[owner.node.uid]?.let { qualifierData ->
            owner.node.copy(
                exportQualifier = qualifierData.jsQualifier?.let { JsModule(it)  },
                immutable = true
            )
        } ?: owner.node

        return super.lowerVariableNode(owner.copy(node = nodeResolved))
    }
}

class ResolveModuleAnnotations : NodeLowering {
    override fun lower(source: SourceSetNode): SourceSetNode {
        val qualifierDataMap = mutableMapOf<String, QualifierData>()
        val sourceSet = source.resolveModuleAnnotations(qualifierDataMap)
        return sourceSet.copy(sources = sourceSet.sources.map { sourceFileNode ->
            sourceFileNode.copy(root = ResolveQualifierAnnotations(qualifierDataMap).lowerRoot(sourceFileNode.root, NodeOwner(sourceFileNode.root, null)))
        })
    }
}