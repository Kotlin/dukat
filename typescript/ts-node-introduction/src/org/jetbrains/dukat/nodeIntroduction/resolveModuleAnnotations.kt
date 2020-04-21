package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ExportableNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.export.JsModule
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.process
import org.jetbrains.dukat.astCommon.rightMost
import org.jetrbains.dukat.nodeLowering.lowerings.NodeLowering

private data class ExportTable(
        val assignExports: MutableMap<String, ModuleNode>
)

private fun buildExportAssignmentTable(docRoot: ModuleNode, exported: ExportTable = ExportTable(mutableMapOf())): ExportTable {
    docRoot.export?.let {
        if (it.isExportEquals) {
            exported.assignExports[it.name] = docRoot
        }
    }

    docRoot.declarations.forEach { declaration ->
        if (declaration is ModuleNode) {
            buildExportAssignmentTable(declaration, exported)
        }
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

        docRoot.declarations.forEach { declaration ->
            val (assignExports) = myExports
            val exportOwner = assignExports[declaration.uid]
            when (declaration) {
                is FunctionNode -> {
                    exportOwner?.let {
                        exportOwner.moduleName?.let { moduleName ->

                            docRoot.declarations
                                    .filterIsInstance(FunctionNode::class.java)
                                    .filter { it.name == declaration.name }
                                    .forEach { functionNode ->
                                        functionNode.exportQualifier = JsModule(moduleName)
                                    }


                            docRoot.removeExportQualifiers()

                            docRoot.declarations.filterIsInstance(ModuleNode::class.java).firstOrNull() { submodule ->
                                submodule.qualifiedPackageName.rightMost() == declaration.name
                            }?.let { eponymousDeclaration ->
                                mergedDocs.put(eponymousDeclaration.uid, docRoot.moduleName)
                            }
                        }
                    }
                }
                is VariableNode -> {
                    exportOwner?.let {
                        exportOwner.moduleName?.let {
                            declaration.exportQualifier = JsModule(it)

                            if (exportOwner.moduleNameIsStringLiteral && (exportOwner.uid == docRoot.uid)) {
                                declaration.name = exportOwner.qualifiedPackageName
                            }

                            declaration.immutable = true

                            docRoot.removeExportQualifiers()
                        }
                    }
                }
                is ClassNode -> {
                    exportOwner?.let {
                        exportOwner.moduleName?.let {
                            declaration.exportQualifier = JsModule(it)

                            docRoot.removeExportQualifiers()
                        }
                    }
                }
            }
        }

        val declarationsResolved = docRoot.declarations.map { declaration ->
            when (declaration) {
                is ModuleNode -> lower(declaration, mergedDocs)
                else -> declaration
            }
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