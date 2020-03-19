package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.ExportAssignmentNode
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
        val assignments: MutableMap<String, DocumentRootNode>,
        val nonAssignments: MutableMap<String, DocumentRootNode>
)

private fun buildExportAssignmentTable(docRoot: DocumentRootNode, exported: ExportTable = ExportTable(mutableMapOf(), mutableMapOf())): ExportTable {
    docRoot.declarations.forEach { declaration ->
        when (declaration) {
            is DocumentRootNode -> buildExportAssignmentTable(declaration, exported)
            is ExportAssignmentNode -> {
                if (declaration.isExportEquals) {
                    exported.assignments[declaration.name] = docRoot
                } else {
                    exported.nonAssignments[declaration.name] = docRoot
                }
            }
        }
    }

    return exported
}

// TODO: duplication, think of separate place to have this (but please don't call it utils )))
private fun unquote(name: String): String {
    return name.replace("(?:^[\"|\'`])|(?:[\"|\'`]$)".toRegex(), "")
}

private fun DocumentRootNode.removeExportQualifiers() {
    jsQualifier = null
    jsModule = null
}

private class ExportAssignmentLowering(
        private val root: DocumentRootNode
) {
    private val myExports: ExportTable = buildExportAssignmentTable(root)

    fun lower(): DocumentRootNode {
        val mergedDocs = mutableMapOf<String, NameEntity?>()
        val doc = lower(root, mergedDocs)
        mergeDocumentRoot(doc, mergedDocs)
        return doc
    }

    private fun mergeDocumentRoot(docRoot: DocumentRootNode, mergedDocs: MutableMap<String, NameEntity?>) {
        if (mergedDocs.contains(docRoot.uid)) {
            mergedDocs[docRoot.uid]?.let { qualifiedName ->
                docRoot.jsQualifier = null
                docRoot.jsModule = qualifiedName
            }
        }

        docRoot.declarations.filterIsInstance(DocumentRootNode::class.java).forEach { mergeDocumentRoot(it, mergedDocs) }
    }

    fun lower(docRoot: DocumentRootNode, mergedDocs: MutableMap<String, NameEntity?>): DocumentRootNode {
        if (myExports.assignments.contains(docRoot.uid)) {
            myExports.assignments[docRoot.uid]?.let { exportOwner ->
                docRoot.jsModule = exportOwner.moduleName
            }
        } else {
            if (docRoot.uid != root.uid) {
                if (docRoot.external) {
                    docRoot.jsModule = docRoot.packageName.process { unquote(it) }
                } else {
                    docRoot.jsQualifier = docRoot.qualifiedPackageName.process { unquote(it) }
                }
            }
        }

        val declarationsResolved = docRoot.declarations.mapNotNull { declaration ->
            val assignments = myExports.assignments
            val nonAssignments = myExports.nonAssignments
            when (declaration) {
                is DocumentRootNode -> lower(declaration, mergedDocs)
                is FunctionNode -> {
                    assignments[declaration.uid]?.let { exportOwner ->
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

                            docRoot.declarations.filterIsInstance(DocumentRootNode::class.java).firstOrNull() { submodule ->
                                submodule.qualifiedPackageName.rightMost() == declaration.name
                            }?.let { eponymousDeclaration ->
                                mergedDocs.put(eponymousDeclaration.uid, docRoot.moduleName)
                            }
                        }
                    }

                    if (!declaration.export) {
                        if (nonAssignments.containsKey(declaration.uid)) {
                            declaration.exportQualifier = JsDefault()
                        }
                    }

                    declaration
                }
                is VariableNode -> {
                    assignments[declaration.uid]?.let { exportOwner ->
                        exportOwner.moduleName?.let {
                            declaration.exportQualifier = JsModule(it)

                            if (exportOwner.external && (exportOwner.uid == docRoot.uid)) {
                                declaration.name = exportOwner.qualifiedPackageName
                            }

                            declaration.immutable = true
                            exportOwner.jsModule = null

                            docRoot.removeExportQualifiers()
                        }
                    }

                    if (nonAssignments.containsKey(declaration.uid)) {
                        declaration.exportQualifier = JsDefault()
                    }

                    declaration
                }
                is ClassNode -> {
                    assignments[declaration.uid]?.let { exportOwner ->
                        exportOwner.moduleName?.let {
                            declaration.exportQualifier = JsModule(it)
                            exportOwner.jsModule = null

                            docRoot.removeExportQualifiers()
                        }
                    }
                    declaration
                }
                is InterfaceNode -> {
                    assignments[declaration.uid]?.let { exportOwner ->
                        exportOwner.moduleName?.let {
                            declaration.exportQualifier = JsModule(it)
                            exportOwner.jsModule = null

                            docRoot.removeExportQualifiers()
                        }
                    }
                    declaration
                }
                is ExportAssignmentNode -> {
                    null
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


private fun DocumentRootNode.resolveModuleAnnotations(): DocumentRootNode {
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