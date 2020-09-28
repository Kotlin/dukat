package org.jetbrains.dukat.nodeIntroduction

import MergeableDeclaration
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.process
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.tsmodel.ExportQualifier
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.JsDefault
import org.jetbrains.dukat.tsmodel.JsModule
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclarationKind
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.WithModifiersDeclaration

fun String.unquote(): String {
    return replace("(?:^[\"\'])|(?:[\"\']$)".toRegex(), "")
}

private fun WithModifiersDeclaration.resolveAsJsDefaultQualifier(): ExportQualifier? {
    return if (hasDefaultModifier() && hasExportModifier()) {
        JsDefault
    } else null
}

private abstract class ExportQualifierBuilder(
        root: ModuleDeclaration,
        private val exportQualifierMap: MutableMap<String?, ExportQualifier>
) {
    init {
        scanModules(root, exportQualifierMap)
        scanTopLevelNodes(root, exportQualifierMap)
    }

    private fun scanModules(docRoot: ModuleDeclaration, assignExports: MutableMap<String?, ExportQualifier> = mutableMapOf(), nested: Boolean = false): Map<String?, ExportQualifier> {
        val exports = docRoot.export
        val moduleName = docRoot.getModuleName()

        exports?.uids?.forEach { uid ->
            assignExports[uid] = if (exports.isExportEquals) {
                JsModule(moduleName)
            } else JsDefault
        }

        if (nested) {
            val jsModule = exportQualifierMap[docRoot.uid]
            if (jsModule == null) {
                assignExports[docRoot.uid] = if (docRoot.kind == ModuleDeclarationKind.AMBIENT_MODULE) {
                    JsModule(name = docRoot.getModuleName() ?: docRoot.name)
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


    private fun scanTopLevelNodes(docRoot: ModuleDeclaration, assignExports: MutableMap<String?, ExportQualifier> = mutableMapOf()): Map<String?, ExportQualifier> {
        val moduleName = docRoot.getModuleName()

        docRoot.declarations.forEach { declaration ->
            if (declaration is ModuleDeclaration) {
                scanTopLevelNodes(declaration, assignExports)
            } else if (declaration is WithModifiersDeclaration) {
                if ((declaration is MergeableDeclaration) && (declaration !is InterfaceDeclaration)) {
                    declaration.resolveAsJsDefaultQualifier()?.let {
                        assignExports.getOrPut(declaration.uid) { it }
                    }

                    val exportQualifier = assignExports[declaration.uid]

                    if (exportQualifier != null) {
                        when (exportQualifier) {
                            is JsDefault -> {
                                assignExports[docRoot.uid] = JsModule(moduleName)
                            }
                            is JsModule -> {
                                assignExports.remove(docRoot.uid)
                            }
                        }
                    }
                }
            }
        }

        return assignExports
    }

    abstract fun ModuleDeclaration.getModuleName(): String?
}

class ExportQualifierMapBuilder(private val moduleNameResolver: ModuleNameResolver)  {
    val exportQualifierMap = mutableMapOf<String?, ExportQualifier>()

    fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.copy(sources = source.sources.map { sourceFileNode ->

            object : ExportQualifierBuilder(sourceFileNode.root, exportQualifierMap) {
                override fun ModuleDeclaration.getModuleName(): String? {
                    return if (kind == ModuleDeclarationKind.AMBIENT_MODULE) {
                        if (name.startsWith("/")) {
                            moduleNameResolver.resolveName(name)
                        } else {
                            name.unquote()
                        }
                    } else {
                        moduleNameResolver.resolveName(sourceName)
                    }
                }
            }

            sourceFileNode
        })
    }
}

