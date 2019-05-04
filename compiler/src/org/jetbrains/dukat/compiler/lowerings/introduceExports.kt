package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.NameNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.ast.model.nodes.translate
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

fun introduceExportAnnotations(docRoot: DocumentRootNode, uidTable: Map<String, TopLevelDeclaration>, turnOff: MutableSet<NameNode>, exportedModules: MutableMap<String, NameNode?>): DocumentRootNode {

    val declarations = docRoot.declarations.mapNotNull { declaration ->
        when (declaration) {
            is DocumentRootNode -> introduceExportAnnotations(declaration, uidTable, turnOff, exportedModules)

            is ExportAssignmentDeclaration -> {
                val defaultAnnotation = AnnotationNode("JsName", listOf(IdentifierNode("default")))


                if (!declaration.isExportEquals) {
                    uidTable.get(declaration.name)?.let { entity ->
                        when (entity) {
                            is FunctionNode -> {
                                if (!entity.export) {
                                    entity.annotations.add(defaultAnnotation)
                                } else Unit
                            }
                            is VariableNode -> entity.annotations.add(defaultAnnotation)
                            else -> Unit
                        }
                    }

                    null
                } else {
                    val entity = uidTable.get(declaration.name)

                    when (entity) {
                        is DocumentRootNode -> {
                            if (docRoot.qualifiedNode != null) {
                                exportedModules[entity.uid] = docRoot.qualifiedNode!!
                            }
                            null
                        }
                        is ClassNode -> {

                            entity.owner?.let {
                                turnOff.add(it.fullPackageName)
                            }

                            if (docRoot.owner != null) {
                                entity.annotations.add(AnnotationNode("JsModule", listOf(docRoot.qualifiedNode!!)))
                            }
                            null
                        }
                        is InterfaceNode -> {
                            entity.owner?.let {
                                turnOff.add(it.fullPackageName)
                            }

                            if (docRoot.owner != null) {
                                entity.annotations.add(AnnotationNode("JsModule", listOf(docRoot.qualifiedNode!!)))
                            }
                            null
                        }
                        is FunctionNode -> {
                            entity.owner?.let { ownerModule ->
                                turnOff.add(ownerModule.fullPackageName)

                                ownerModule.declarations.filterIsInstance(DocumentRootNode::class.java).firstOrNull() { submodule ->
                                    submodule.packageName == entity.name.translate()
                                }?.let { eponymousDeclaration ->
                                    exportedModules.put(eponymousDeclaration.uid, ownerModule.qualifiedNode)
                                }
                            }

                            //TODO: investigate how set annotations only at FunctionNode only
                            docRoot.declarations.filterIsInstance(FunctionNode::class.java).forEach {
                                if (it != entity) {
                                    it.annotations.add(AnnotationNode("JsModule", listOf(docRoot.qualifiedNode!!)))

                                }
                            }

                            if (docRoot.owner != null) {
                                entity.annotations.add(AnnotationNode("JsModule", listOf(docRoot.qualifiedNode!!)))
                            }
                            null
                        }
                        is VariableNode -> {
                            entity.owner?.let {
                                turnOff.add(it.fullPackageName)
                            }


                            if (docRoot.uid == entity.owner?.uid) {
                                if (docRoot.qualifiedNode != null) {
                                    entity.name = docRoot.qualifiedNode!!
                                }
                            }

                            if (docRoot.owner != null) {
                                entity.annotations.add(AnnotationNode("JsModule", listOf(docRoot.qualifiedNode!!)))
                                entity.immutable = true
                            }

                            null
                        }
                        else -> declaration
                    }
                }

            }

            else -> declaration
        }
    }

    return docRoot.copy(declarations = declarations)
}



private fun DocumentRootNode.turnOff(turnOffData: MutableSet<NameNode>): DocumentRootNode {
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

private fun DocumentRootNode.markModulesAsExported(exportedModulesData: Map<String, NameNode?>): DocumentRootNode {
    if (exportedModulesData.containsKey(uid)) {
        qualifiedNode = exportedModulesData.getValue(uid)
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
    val turnOffData = mutableSetOf<NameNode>()
    val exportedModulesData = mutableMapOf<String, NameNode?>()
    val docRoot =  introduceExportAnnotations(this, uidTable, turnOffData, exportedModulesData)

    return docRoot.turnOff(turnOffData).markModulesAsExported(exportedModulesData)
}

fun SourceSetNode.introduceExports() = transform { it.introduceExports() }