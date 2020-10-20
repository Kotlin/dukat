package org.jetbrains.dukat.nodeIntroduction

import MergeableDeclaration
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.JsModule
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclarationKind
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.WithModifiersDeclaration

private class ExportQualifierBuilder(
        root: ModuleDeclaration,
        private val exportQualifierMap: MutableMap<String?, JsModule>,
        private val moduleNameResolver: ModuleNameResolver
) {
    init {
        scanModules(root, exportQualifierMap)
        scanTopLevelNodes(root, exportQualifierMap)
    }

    private fun scanModules(docRoot: ModuleDeclaration, assignExports: MutableMap<String?, JsModule> = mutableMapOf(), nested: Boolean = false): Map<String?, JsModule> {
        val exports = docRoot.export
        val moduleName = moduleNameResolver.resolveName(docRoot)

        if (exports?.isExportEquals == true) {
            exports.uids.forEach { uid ->
                assignExports[uid] = JsModule(moduleName)
            }
        }

        if (nested) {
            val jsModule = exportQualifierMap[docRoot.uid]
            if (jsModule == null) {
                assignExports[docRoot.uid] = if ((docRoot.kind == ModuleDeclarationKind.AMBIENT_MODULE) || (docRoot.kind == ModuleDeclarationKind.AMBIENT_FILE_PATH)) {
                    JsModule(name = moduleName ?: docRoot.name)
                } else {
                    JsModule(
                            name = null,
                            qualifier = true
                    )
                }
            }
        }

        docRoot.declarations.filterIsInstance(ModuleDeclaration::class.java).forEach {
            (it as? ModuleDeclaration)?.let { moduleDeclaration -> scanModules(moduleDeclaration, assignExports, true) }
        }

        return assignExports
    }


    private fun scanTopLevelNodes(docRoot: ModuleDeclaration, assignExports: MutableMap<String?, JsModule> = mutableMapOf()): Map<String?, JsModule> {
        docRoot.declarations.forEach { declaration ->
            if (declaration is ModuleDeclaration) {
                scanTopLevelNodes(declaration, assignExports)
            } else if (declaration is WithModifiersDeclaration) {
                if ((declaration is MergeableDeclaration) && (declaration !is InterfaceDeclaration)) {
                    val exportQualifier = assignExports[declaration.uid]

                    if (exportQualifier != null) {
                        assignExports.remove(docRoot.uid)
                    }
                }
            }
        }

        return assignExports
    }
}

class ExportQualifierMapBuilder(private val moduleNameResolver: ModuleNameResolver)  {
    val exportQualifierMap = mutableMapOf<String?, JsModule>()

    fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.copy(sources = source.sources.map { sourceFileNode ->
            ExportQualifierBuilder(sourceFileNode.root, exportQualifierMap, moduleNameResolver)
            sourceFileNode
        })
    }
}

