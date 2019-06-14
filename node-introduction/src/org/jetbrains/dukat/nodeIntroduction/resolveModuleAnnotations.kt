package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.export.JsDefault
import org.jetbrains.dukat.ast.model.nodes.export.JsModule
import org.jetbrains.dukat.ast.model.nodes.processing.ROOT_PACKAGENAME
import org.jetbrains.dukat.ast.model.nodes.processing.process
import org.jetbrains.dukat.ast.model.nodes.processing.rightMost
import org.jetbrains.dukat.ast.model.nodes.processing.translate
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.tsmodel.ExportAssignmentDeclaration

private data class ExportTable(
        val assignments: MutableMap<String, DocumentRootNode>,
        val nonAssignments: MutableMap<String, DocumentRootNode>
)

private fun buildExportAssignmentTable(docRoot: DocumentRootNode, exported: ExportTable = ExportTable(mutableMapOf(), mutableMapOf())): ExportTable {
    docRoot.declarations.forEach { declaration ->
        when (declaration) {
            is DocumentRootNode -> buildExportAssignmentTable(declaration, exported)
            is ExportAssignmentDeclaration -> {
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
        private val root: DocumentRootNode,
        private val moduleNameResolver: ModuleNameResolver
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

    private fun DocumentRootNode.resolveModule(): NameEntity? {
        return if (external) {
            packageName.process { unquote(it) }
        } else {
            moduleNameResolver.resolveName(fileName)
        }
    }

    fun lower(docRoot: DocumentRootNode, mergedDocs: MutableMap<String, NameEntity?>): DocumentRootNode {
        if (myExports.assignments.contains(docRoot.uid)) {
            myExports.assignments[docRoot.uid]?.let { exportOwner ->
                docRoot.jsModule = exportOwner.resolveModule()
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
                        exportOwner.resolveModule()?.let { moduleName ->

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
                                mergedDocs.put(eponymousDeclaration.uid, docRoot.resolveModule())
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
                        exportOwner.resolveModule()?.let {
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
                        exportOwner.resolveModule()?.let {
                            declaration.exportQualifier = JsModule(it)
                            exportOwner.jsModule = null

                            docRoot.removeExportQualifiers()
                        }
                    }
                    declaration
                }
                is InterfaceNode -> {
                    assignments[declaration.uid]?.let { exportOwner ->
                        exportOwner.resolveModule()?.let {
                            declaration.exportQualifier = JsModule(it)
                            exportOwner.jsModule = null

                            docRoot.removeExportQualifiers()
                        }
                    }
                    declaration
                }
                is ExportAssignmentDeclaration -> {
                    null
                }
                else -> {
                    declaration
                }
            }
        }

        return docRoot.copy(declarations = declarationsResolved)
    }
}


fun DocumentRootNode.resolveModuleAnnotations(moduleNameResolver: ModuleNameResolver): DocumentRootNode {
    return ExportAssignmentLowering(this, moduleNameResolver).lower()
}

fun SourceSetNode.resolveModuleAnnotations(moduleNameResolver: ModuleNameResolver) = transform {
    it.resolveModuleAnnotations(moduleNameResolver)
}
