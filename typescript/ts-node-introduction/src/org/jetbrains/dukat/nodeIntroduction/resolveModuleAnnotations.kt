package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.export.JsDefault
import org.jetbrains.dukat.ast.model.nodes.export.JsModule
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.process
import org.jetbrains.dukat.astCommon.rightMost
import org.jetrbains.dukat.nodeLowering.lowerings.NodeLowering

private data class ExportTable(
        val assignExports: MutableMap<String, ModuleNode>,
        val defaultExports: MutableMap<String, ModuleNode>
)

private fun buildExportAssignmentTable(docRoot: ModuleNode, exported: ExportTable = ExportTable(mutableMapOf(), mutableMapOf())): ExportTable {
    docRoot.export?.let {
        if (it.isExportEquals) {
            exported.assignExports[it.name] = docRoot
        } else {
            exported.defaultExports[it.name] = docRoot
        }
    }

    docRoot.declarations.forEach { declaration ->
        if (declaration is ModuleNode) { buildExportAssignmentTable(declaration, exported) }
    }

    return exported
}

// TODO: duplication, think of separate place to have this (but please don't call it utils )))
private fun unquote(name: String): String {
    return name.replace("(?:^[\"|\'`])|(?:[\"|\'`]$)".toRegex(), "")
}

private fun ModuleNode.removeExportQualifiers() {
    jsQualifier = null
    jsModule = null
}

private class ExportAssignmentLowering(
        private val root: ModuleNode
) {
    private val myExports: ExportTable = buildExportAssignmentTable(root)

    fun lower(): ModuleNode {
        val mergedDocs = mutableMapOf<String, NameEntity?>()
        val doc = lower(root, mergedDocs)
        mergeDocumentRoot(doc, mergedDocs)
        return doc
    }

    private fun mergeDocumentRoot(docRoot: ModuleNode, mergedDocs: MutableMap<String, NameEntity?>) {
        if (mergedDocs.contains(docRoot.uid)) {
            mergedDocs[docRoot.uid]?.let { qualifiedName ->
                docRoot.jsQualifier = null
                docRoot.jsModule = qualifiedName
            }
        }

        docRoot.declarations.filterIsInstance(ModuleNode::class.java).forEach { mergeDocumentRoot(it, mergedDocs) }
    }

    fun lower(docRoot: ModuleNode, mergedDocs: MutableMap<String, NameEntity?>): ModuleNode {
        if (myExports.assignExports.contains(docRoot.uid)) {
            myExports.assignExports[docRoot.uid]?.let { exportOwner ->
                docRoot.jsModule = exportOwner.moduleName
            }
        } else {
            if (docRoot.uid != root.uid) {
                if (docRoot.moduleNameIsStringLiteral) {
                    docRoot.jsModule = docRoot.packageName.process { unquote(it) }
                } else {
                    docRoot.jsQualifier = docRoot.qualifiedPackageName.process { unquote(it) }
                }
            }
        }

        val declarationsResolved = docRoot.declarations.map { declaration ->
            val (assignExports, defaultExports) = myExports
            when (declaration) {
                is ModuleNode -> lower(declaration, mergedDocs)
                is FunctionNode -> {
                    assignExports[declaration.uid]?.let { exportOwner ->
                        exportOwner.moduleName?.let { moduleName ->

                            docRoot.declarations
                                    .filterIsInstance(FunctionNode::class.java)
                                    .filter { it.name == declaration.name }
                                    .forEach { functionNode ->
                                        functionNode.exportQualifier = JsModule(moduleName)
                                    }

                            exportOwner.jsModule = null
                            exportOwner.jsQualifier = null

                            docRoot.removeExportQualifiers()

                            docRoot.declarations.filterIsInstance(ModuleNode::class.java).firstOrNull() { submodule ->
                                submodule.qualifiedPackageName.rightMost() == declaration.name
                            }?.let { eponymousDeclaration ->
                                mergedDocs.put(eponymousDeclaration.uid, docRoot.moduleName)
                            }
                        }
                    }

                    if (!declaration.export) {
                        if (defaultExports.containsKey(declaration.uid)) {
                            declaration.exportQualifier = JsDefault()
                        }
                    }

                    declaration
                }
                is VariableNode -> {
                    assignExports[declaration.uid]?.let { exportOwner ->
                        exportOwner.moduleName?.let {
                            declaration.exportQualifier = JsModule(it)

                            if (exportOwner.moduleNameIsStringLiteral && (exportOwner.uid == docRoot.uid)) {
                                declaration.name = exportOwner.qualifiedPackageName
                            }

                            declaration.immutable = true
                            exportOwner.jsModule = null

                            docRoot.removeExportQualifiers()
                        }
                    }

                    if (defaultExports.containsKey(declaration.uid)) {
                        declaration.exportQualifier = JsDefault()
                    }

                    declaration
                }
                is ClassNode -> {
                    assignExports[declaration.uid]?.let { exportOwner ->
                        exportOwner.moduleName?.let {
                            declaration.exportQualifier = JsModule(it)
                            exportOwner.jsModule = null

                            docRoot.removeExportQualifiers()
                        }
                    }
                    declaration
                }
                is InterfaceNode -> {
                    assignExports[declaration.uid]?.let { exportOwner ->
                        exportOwner.moduleName?.let {
                            declaration.exportQualifier = JsModule(it)
                            exportOwner.jsModule = null

                            docRoot.removeExportQualifiers()
                        }
                    }
                    declaration
                }
                else -> {
                    declaration
                }
            }
        }

        val hasDefaults = docRoot.declarations.any { declaration ->
            when (declaration) {
                is ClassNode -> declaration.exportQualifier is JsDefault
                is FunctionNode -> declaration.exportQualifier is JsDefault
                is InterfaceNode -> declaration.exportQualifier is JsDefault
                is VariableNode -> declaration.exportQualifier is JsDefault
                else -> false
            }
        }

        if (hasDefaults && (docRoot.jsModule == null)) {
            docRoot.jsModule = docRoot.moduleName
        }

        return docRoot.copy(declarations = declarationsResolved)
    }
}


private fun ModuleNode.resolveModuleAnnotations(): ModuleNode {
    return ExportAssignmentLowering(this).lower()
}


private fun SourceSetNode.resolveModuleAnnotations() = transform {
    it.resolveModuleAnnotations()
}

class ResolveModuleAnnotations() : NodeLowering {
    override fun lower(source: SourceSetNode): SourceSetNode {
        return source.resolveModuleAnnotations()
    }
}