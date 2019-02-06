package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.ExportAssignmentDeclaration


fun buildUidTable(docRoot: DocumentRootNode, map: MutableMap<String, TopLevelDeclaration> = mutableMapOf()): Map<String, TopLevelDeclaration> {
    map[docRoot.uid] = docRoot

    docRoot.declarations.forEach { declaration ->
        when (declaration) {
            is InterfaceNode -> map[declaration.uid] = declaration
            is ClassNode -> map[declaration.uid] = declaration
            is FunctionNode -> map[declaration.uid] = declaration
            is VariableNode -> map[declaration.uid] = declaration
            is DocumentRootNode -> buildUidTable(declaration, map)
            else -> Unit
        }
    }

    return map
}

fun introduceExportAnnotations(docRoot: DocumentRootNode, uidTable: Map<String, TopLevelDeclaration>, turnOff: MutableSet<String>, exportedModules: MutableMap<String, String>): DocumentRootNode {

    val declarations = docRoot.declarations.map { declaration ->
        when (declaration) {
            is DocumentRootNode -> listOf(introduceExportAnnotations(declaration, uidTable, turnOff, exportedModules))

            is ExportAssignmentDeclaration -> {
                val defaultAnnotation = AnnotationNode("JsName", listOf("default"))


                if (!declaration.isExportEquals) {
                    uidTable.get(declaration.name)?.let { entity ->
                        when (entity) {
                            is FunctionNode -> {
                                if (!entity.isExport) {
                                    entity.annotations.add(defaultAnnotation)
                                } else Unit
                            }
                            is VariableNode -> entity.annotations.add(defaultAnnotation)
                            else -> Unit
                        }
                    }

                    listOf(declaration)
                } else {
                    val entity = uidTable.get(declaration.name)

                    when (entity) {
                        is DocumentRootNode -> {
                            exportedModules.put(entity.uid, docRoot.qualifierName)
                            emptyList()
                        }
                        is ClassNode -> {

                            entity.owner?.let {
                                turnOff.add(it.fullPackageName)
                            }

                            if (docRoot.owner != null) {
                                entity.annotations.add(AnnotationNode("JsModule", listOf(docRoot.qualifierName)))
                            }
                            emptyList<TopLevelDeclaration>()
                        }
                        is InterfaceNode -> {
                            entity.owner?.let {
                                turnOff.add(it.fullPackageName)
                            }

                            if (docRoot.owner != null) {
                                entity.annotations.add(AnnotationNode("JsModule", listOf(docRoot.qualifierName)))
                            }
                            emptyList()

                        }
                        is FunctionNode -> {
                            entity.owner?.let {
                                turnOff.add(it.fullPackageName)
                            }

                            if (docRoot.owner != null) {
                                entity.annotations.add(AnnotationNode("JsModule", listOf(docRoot.qualifierName)))
                            }
                            emptyList()
                        }
                        is VariableNode -> {
                            entity.owner?.let {
                                turnOff.add(it.fullPackageName)
                            }

                            if (docRoot.owner != null) {
                                entity.annotations.add(AnnotationNode("JsModule", listOf(docRoot.qualifierName)))
                                entity.immutable = true
                            }
                            emptyList()
                        }
                        else -> listOf(declaration)
                    }
                }

            }

            else -> listOf(declaration)
        }
    }.flatten()

    return docRoot.copy(declarations = declarations)
}



private fun DocumentRootNode.turnOff(turnOffData: MutableSet<String>): DocumentRootNode {
    if (turnOffData.contains(fullPackageName)) {
        showQualifierAnnotation = false
    }

    val declarations = declarations.map { declaration ->
        when (declaration) {
            is DocumentRootNode -> {
            declaration.turnOff(turnOffData)
            }
            else -> declaration
        }
    }

    return copy(declarations = declarations)
}

private fun DocumentRootNode.markModulesAsExported(exportedModulesData: Map<String, String>): DocumentRootNode {
    if (exportedModulesData.containsKey(uid)) {
        qualifierName = exportedModulesData.getValue(uid)
        isQualifier = false
    }

    val declarations = declarations.map { declaration ->
        when (declaration) {
            is DocumentRootNode -> {
                declaration.markModulesAsExported(exportedModulesData)
            }
            else -> declaration
        }
    }

    return copy(declarations = declarations)
}

fun DocumentRootNode.introduceExports(): DocumentRootNode {
    val uidTable = buildUidTable(this)
    val turnOffData = mutableSetOf<String>()
    val exportedModulesData = mutableMapOf<String, String>()
    val docRoot =  introduceExportAnnotations(this, uidTable, turnOffData, exportedModulesData)

    return docRoot.turnOff(turnOffData).markModulesAsExported(exportedModulesData)
}