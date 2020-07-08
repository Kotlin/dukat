package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.ExportableNode
import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.export.JsModule
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.astCommon.process
import org.jetrbains.dukat.nodeLowering.lowerings.NodeLowering

private fun buildExportAssignmentTable(docRoot: ModuleNode, assignExports: MutableMap<String, ModuleNode> = mutableMapOf()): Map<String, ModuleNode> {
     val exports = docRoot.export
    if (exports?.isExportEquals == true) {
        exports.uids.forEach { uid ->
            assignExports[uid] = docRoot
        }
    }

    docRoot.declarations.forEach { declaration ->
        if (declaration is ModuleNode) {
            buildExportAssignmentTable(declaration, assignExports)
        }
    }

    return assignExports
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
    private val assignExports: Map<String, ModuleNode> = buildExportAssignmentTable(root)

    fun lower(): ModuleNode {
        return lower(root)
    }

    fun lower(docRoot: ModuleNode): ModuleNode {
        if (assignExports.contains(docRoot.uid)) {
            assignExports[docRoot.uid]?.let { exportOwner ->
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
            val exportOwner = assignExports[declaration.uid]
            exportOwner?.moduleName?.let { moduleName ->
                if (declaration is ExportableNode) {
                    declaration.exportQualifier = JsModule(moduleName)
                    docRoot.removeExportQualifiers()

                    if (declaration is VariableNode) {
                        if (exportOwner.moduleNameIsStringLiteral && (exportOwner.uid == docRoot.uid)) {
                            declaration.name = exportOwner.qualifiedPackageName
                        }

                        declaration.immutable = true
                    }
                }
            }
        }

        val declarationsResolved = docRoot.declarations.map { declaration ->
            when (declaration) {
                is ModuleNode -> lower(declaration)
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

class ResolveModuleAnnotations : NodeLowering {
    override fun lower(source: SourceSetNode): SourceSetNode {
        return source.resolveModuleAnnotations()
    }
}