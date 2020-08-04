package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.astCommon.isStringLiteral
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.ast.model.nodes.export.ExportQualifier
import org.jetbrains.dukat.ast.model.nodes.export.JsDefault
import org.jetbrains.dukat.ast.model.nodes.export.JsModule
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.process
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetrbains.dukat.nodeLowering.NodeWithOwnerTypeLowering
import org.jetrbains.dukat.nodeLowering.lowerings.NodeLowering

private fun String.unquote(): String {
    return replace("(?:^[\"\'])|(?:[\"\']$)".toRegex(), "")
}

private fun NodeOwner<*>.moduleOwner(): ModuleNode? {
    return generateSequence(owner) { it.owner }.firstOrNull { it.node is ModuleNode }?.node as? ModuleNode
}

private class ExportAssignmentLowering(
        private val root: ModuleNode,
        private val exportQualifierMap: MutableMap<String?, ExportQualifier>,
        private val moduleNameResolver: ModuleNameResolver
) : NodeWithOwnerTypeLowering {
    private val assignExports: Map<String, ExportQualifier> = buildExportAssignmentTable(root)

    private fun buildExportAssignmentTable(docRoot: ModuleNode, assignExports: MutableMap<String, ExportQualifier> = mutableMapOf()): Map<String, ExportQualifier> {
        val exports = docRoot.export
        if (exports?.isExportEquals == true) {
            exports.uids.forEach { uid ->
                docRoot.getModuleName()?.let { assignExports[uid] = JsModule(it) }
            }
        } else {
            exports?.uids?.forEach { uid ->
                docRoot.getModuleName()?.let { assignExports[uid] = JsDefault }
            }
        }

        docRoot.declarations.forEach { declaration ->
            if (declaration is ModuleNode) {
                buildExportAssignmentTable(declaration, assignExports)
            }
        }

        return assignExports
    }


    private fun ModuleNode.getModuleName(): NameEntity? {
        return if (packageName.isStringLiteral()) {
            packageName.process { it.unquote() }
        } else {
            moduleNameResolver.resolveName(fileName)?.let { IdentifierEntity(it) }
        }
    }

    override fun lowerRoot(moduleNode: ModuleNode, owner: NodeOwner<ModuleNode>): ModuleNode {
        if (assignExports.contains(moduleNode.uid)) {
            (assignExports[moduleNode.uid] as? JsModule)?.let { jsModule ->
                exportQualifierMap[moduleNode.uid] = JsModule(
                        name = jsModule.name,
                        qualifier = false
                )
            }
        } else {
            if (moduleNode.uid != root.uid) {
                exportQualifierMap[moduleNode.uid] = if (moduleNode.packageName.isStringLiteral()) {
                    JsModule(
                            name = moduleNode.packageName
                    )
                } else {
                    JsModule(
                            name = null,
                            qualifier = true
                    )
                }
            }
        }

        return super.lowerRoot(moduleNode, owner)
    }

    override fun lowerFunctionNode(owner: NodeOwner<FunctionNode>): FunctionNode {
        val declaration = owner.node
        val exportQualifier = assignExports[declaration.uid]

        val moduleNode = owner.moduleOwner()
        when (exportQualifier) {
            is JsModule -> {
                exportQualifierMap[declaration.uid] = JsModule(exportQualifier.name)
                exportQualifierMap[moduleNode?.uid] = JsModule(null)
            }
            is JsDefault -> {
                exportQualifierMap[moduleNode?.uid] = JsModule(moduleNode?.getModuleName())
            }
        }


        return declaration
    }

    override fun lowerClassNode(owner: NodeOwner<ClassNode>): ClassNode {
        val declaration = owner.node
        val exportQualifier = assignExports[declaration.uid]

        val moduleNode = owner.moduleOwner()
        when (exportQualifier) {
            is JsModule -> {
                exportQualifierMap[declaration.uid] = JsModule(exportQualifier.name)
                exportQualifierMap[moduleNode?.uid] = JsModule(null)
            }
            is JsDefault -> {
                exportQualifierMap[moduleNode?.uid] = JsModule(moduleNode?.getModuleName())
            }
        }

        return declaration
    }

    override fun lowerVariableNode(owner: NodeOwner<VariableNode>): VariableNode {
        val declaration = owner.node
        val exportQualifier = assignExports[declaration.uid]

        val moduleNode = owner.moduleOwner()

        when (exportQualifier) {
            is JsModule -> {
                exportQualifierMap[declaration.uid] = JsModule(exportQualifier.name)
                exportQualifierMap[moduleNode?.uid] = JsModule(null)

                if (moduleNode?.packageName?.isStringLiteral() == true) {
                    exportQualifier.name?.let { declaration.name = it }
                }
            }
            is JsDefault -> {
                exportQualifierMap[moduleNode?.uid] = JsModule(moduleNode?.getModuleName())
            }
        }

        return declaration
    }
}

internal class ExportQualifierMapBuilder(private val moduleNameResolver: ModuleNameResolver) : NodeLowering {
    val exportQualifierMap = mutableMapOf<String?, ExportQualifier>()

    override fun lower(source: SourceSetNode): SourceSetNode {
        return source. copy(sources = source.sources.map { sourceFileNode ->
            val root = sourceFileNode.root
            sourceFileNode.copy(root = ExportAssignmentLowering(root, exportQualifierMap, moduleNameResolver).lowerRoot(root, NodeOwner(root, null)))
        })
    }
}

