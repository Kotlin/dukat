package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.isStringLiteral
import org.jetbrains.dukat.astCommon.process
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.tsLowerings.DeclarationLowering
import org.jetbrains.dukat.tsLowerings.TsLowering
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ExportQualifier
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.FunctionOwnerDeclaration
import org.jetbrains.dukat.tsmodel.JsDefault
import org.jetbrains.dukat.tsmodel.JsModule
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration

private fun String.unquote(): String {
    return replace("(?:^[\"\'])|(?:[\"\']$)".toRegex(), "")
}

private class ExportAssignmentLowering(
        private val root: ModuleDeclaration,
        private val exportQualifierMap: MutableMap<String?, ExportQualifier>,
        private val moduleNameResolver: ModuleNameResolver,
        private val fileName: String
) : DeclarationLowering {
    private val assignExports: Map<String, ExportQualifier> = buildExportAssignmentTable(root)

    private fun buildExportAssignmentTable(docRoot: ModuleDeclaration, assignExports: MutableMap<String, ExportQualifier> = mutableMapOf()): Map<String, ExportQualifier> {
        val exports = docRoot.export
        if (exports?.isExportEquals == true) {
            exports.uids.forEach { uid ->
                docRoot.getModuleName().let { assignExports[uid] = JsModule(it) }
            }
        } else {
            exports?.uids?.forEach { uid ->
                docRoot.getModuleName().let { assignExports[uid] = JsDefault }
            }
        }

        docRoot.declarations.forEach { declaration ->
            if (declaration is ModuleDeclaration) {
                buildExportAssignmentTable(declaration, assignExports)
            }
        }

        return assignExports
    }

    private fun ModuleDeclaration.getModuleName(): NameEntity? {
        return if (name.isStringLiteral()) {
            name.process { it.unquote() }
        } else {
            moduleNameResolver.resolveName(fileName)?.let { resolvedName -> IdentifierEntity(resolvedName) }
        }
    }

    override fun lowerModuleModel(moduleDeclaration: ModuleDeclaration, owner: NodeOwner<ModuleDeclaration>?): ModuleDeclaration? {
        if (assignExports.contains(moduleDeclaration.uid)) {
            (assignExports[moduleDeclaration.uid] as? JsModule).let { jsModule ->
                exportQualifierMap[moduleDeclaration.uid] = JsModule(
                        name = jsModule?.name,
                        qualifier = false
                )
            }
        } else {
            if (moduleDeclaration.uid != root.uid) {
                exportQualifierMap[moduleDeclaration.uid] = if (moduleDeclaration.name.isStringLiteral()) {
                    JsModule(
                            name = moduleDeclaration.name
                    )
                } else {
                    JsModule(
                            name = null,
                            qualifier = true
                    )
                }
            }
        }

        return super.lowerModuleModel(moduleDeclaration, owner)
    }

    override fun lowerSourceDeclaration(moduleDeclaration: ModuleDeclaration, owner: NodeOwner<ModuleDeclaration>?): ModuleDeclaration {
        if (assignExports.contains(moduleDeclaration.uid)) {
            (assignExports[moduleDeclaration.uid] as? JsModule).let { jsModule ->
                exportQualifierMap[moduleDeclaration.uid] = JsModule(
                        name = jsModule?.name,
                        qualifier = false
                )
            }
        } else {
            if (moduleDeclaration.uid != root.uid) {
                exportQualifierMap[moduleDeclaration.uid] = if (moduleDeclaration.name.isStringLiteral()) {
                    JsModule(
                            name = moduleDeclaration.name
                    )
                } else {
                    JsModule(
                            name = null,
                            qualifier = true
                    )
                }
            }
        }

        return super.lowerSourceDeclaration(moduleDeclaration, owner)
    }

    override fun lowerFunctionDeclaration(declaration: FunctionDeclaration, owner: NodeOwner<FunctionOwnerDeclaration>?): FunctionDeclaration {
        val exportQualifier = assignExports[declaration.uid]

        val moduleNode = owner?.node as? ModuleDeclaration

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

    override fun lowerClassDeclaration(declaration: ClassDeclaration, owner: NodeOwner<ModuleDeclaration>?): ClassDeclaration {
        val exportQualifier = assignExports[declaration.uid]

        val moduleNode = owner?.node

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

    override fun lowerVariableDeclaration(declaration: VariableDeclaration, owner: NodeOwner<ModuleDeclaration>?): VariableDeclaration {
        val exportQualifier = assignExports[declaration.uid]

        val moduleNode = owner?.node

        when (exportQualifier) {
            is JsModule -> {
                exportQualifierMap[declaration.uid] = JsModule(exportQualifier.name)
                exportQualifierMap[moduleNode?.uid] = JsModule(null)

                if (moduleNode?.name?.isStringLiteral() == true) {
                    exportQualifier.name.let { declaration.name = it.toString() }
                }
            }
            is JsDefault -> {
                exportQualifierMap[moduleNode?.uid] = JsModule(moduleNode?.getModuleName())
            }
        }

        return declaration
    }
}

class ExportQualifierMapBuilderDeclaration(private val moduleNameResolver: ModuleNameResolver) : TsLowering {
    val exportQualifierMap = mutableMapOf<String?, ExportQualifier>()

    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.copy(sources = source.sources.map { sourceFileNode ->
            val root = sourceFileNode.root
            sourceFileNode.copy(root = ExportAssignmentLowering(root, exportQualifierMap, moduleNameResolver, sourceFileNode.fileName).lowerSourceDeclaration(root, NodeOwner(root, null)))
        })
    }
}

