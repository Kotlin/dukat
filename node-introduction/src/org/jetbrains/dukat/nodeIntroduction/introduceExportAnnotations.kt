package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.QualifierKind
import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.processing.rightMost
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ExportAssignmentDeclaration

typealias EntityWithOwner = Pair<TopLevelEntity, DocumentRootNode>

private fun buildUidTable(docRoot: DocumentRootNode, map: MutableMap<String, EntityWithOwner> = mutableMapOf()): Map<String, EntityWithOwner> {
    map[docRoot.uid] = Pair(docRoot, docRoot)

    docRoot.declarations.forEach { declaration ->
        when (declaration) {
            is InterfaceNode -> map[declaration.uid] = Pair(declaration, docRoot)
            is ClassNode -> map[declaration.uid] = Pair(declaration, docRoot)
            is FunctionNode -> map[declaration.uid] = Pair(declaration, docRoot)
            is VariableNode -> map[declaration.uid] = Pair(declaration, docRoot)
            is DocumentRootNode -> buildUidTable(declaration, map)
            else -> Unit
        }
    }

    return map
}

private class ExportAnnotationsLowering(private val myUidTable: Map<String, EntityWithOwner>) {
    private val myTurnOffData = mutableSetOf<NameEntity>()
    private val myExportedModulesData = mutableMapOf<String, NameEntity?>()

    private fun lowerExportAssignmentDeclaration(owner: NodeOwner<ExportAssignmentDeclaration>): TopLevelEntity? {
        val defaultAnnotation = AnnotationNode("JsName", listOf(IdentifierEntity("default")))

        val declaration = owner.node
        val docRoot = owner.owner?.node

        if (docRoot !is DocumentRootNode) {
            return null
        }

        return if (!declaration.isExportEquals) {
            myUidTable.get(declaration.name)?.let { (entity, _) ->
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
            val uidRecord = myUidTable.get(declaration.name)
            if (uidRecord == null) {
                return null
            }

            val (entity, entityOwner) = uidRecord

            val rootQualifiedNode = docRoot.qualifiedNode
            when (entity) {
                is DocumentRootNode -> {

                    rootQualifiedNode?.let { qualifiedNode ->
                        myExportedModulesData[entity.uid] = qualifiedNode
                    }

                    null
                }
                is ClassNode -> {

                    myTurnOffData.add(entityOwner.packageName)

                    if (docRoot.owner != null) {
                        rootQualifiedNode?.let { qualifiedNode ->
                            entity.annotations.add(AnnotationNode("JsModule", listOf(qualifiedNode)))
                        }
                    }
                    null
                }
                is InterfaceNode -> {
                    myTurnOffData.add(entityOwner.packageName)

                    if (docRoot.owner != null) {
                        rootQualifiedNode?.let { qualifiedNode ->
                            entity.annotations.add(AnnotationNode("JsModule", listOf(qualifiedNode)))
                        }
                    }
                    null
                }
                is FunctionNode -> {
                    myTurnOffData.add(entityOwner.packageName)

                    entityOwner.declarations.filterIsInstance(DocumentRootNode::class.java).firstOrNull() { submodule ->
                        submodule.packageName.rightMost() == entity.name
                    }?.let { eponymousDeclaration ->
                        myExportedModulesData.put(eponymousDeclaration.uid, entityOwner.qualifiedNode)
                    }

                    //TODO: investigate how set annotations only at FunctionNode only
                    docRoot.declarations.filterIsInstance(FunctionNode::class.java).forEach {
                        if (it != entity) {
                            rootQualifiedNode?.let { qualifiedNode ->
                                it.annotations.add(AnnotationNode("JsModule", listOf(qualifiedNode)))
                            }
                        }
                    }

                    if (docRoot.owner != null) {
                        rootQualifiedNode?.let { qualifiedNode ->
                            entity.annotations.add(AnnotationNode("JsModule", listOf(qualifiedNode)))
                        }
                    }
                    null
                }
                is VariableNode -> {
                    myTurnOffData.add(entityOwner.packageName)

                    if (docRoot.uid == entityOwner.uid) {
                        rootQualifiedNode?.let { qualifiedNode ->
                            entity.name = qualifiedNode
                        }
                    }

                    if (docRoot.owner != null) {
                        rootQualifiedNode?.let { qualifiedNode ->
                            entity.annotations.add(AnnotationNode("JsModule", listOf(qualifiedNode)))
                        }
                        entity.immutable = true
                    }

                    null
                }
                else -> declaration
            }
        }

    }

    fun introduceExportAnnotations(owner: NodeOwner<DocumentRootNode>): DocumentRootNode {
        val docRoot = owner.node

        val declarations = docRoot.declarations.mapNotNull { declaration ->
            when (declaration) {
                is DocumentRootNode -> introduceExportAnnotations(owner.wrap(declaration))
                is ExportAssignmentDeclaration -> lowerExportAssignmentDeclaration(owner.wrap(declaration))
                else -> declaration
            }
        }

        return docRoot.copy(declarations = declarations).turnOff().markModulesAsExported()
    }

    private fun DocumentRootNode.markModulesAsExported(): DocumentRootNode {
        if (myExportedModulesData.containsKey(uid)) {
            qualifiedNode = myExportedModulesData.getValue(uid)
            qualifierKind = QualifierKind.MODULE
        }

        val declarations = declarations.map { declaration ->
            when (declaration) {
                is DocumentRootNode -> {
                    declaration.markModulesAsExported()
                }
                else -> declaration
            }
        }

        return copy(declarations = declarations)
    }


    private fun DocumentRootNode.turnOff(): DocumentRootNode {
        if (myTurnOffData.contains(packageName)) {
            qualifiedNode = null
        }

        val declarations = declarations.map { declaration ->
            when (declaration) {
                is DocumentRootNode -> {
                    declaration.turnOff()
                }
                else -> declaration
            }
        }

        return copy(declarations = declarations)
    }
}

fun DocumentRootNode.introduceExportAnnotations(): DocumentRootNode {
    return ExportAnnotationsLowering(buildUidTable(this)).introduceExportAnnotations(NodeOwner(this, null))
}

fun SourceSetNode.introduceExportAnnotations() = transform { it.introduceExportAnnotations() }