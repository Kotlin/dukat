package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.ExportAssignmentDeclaration


fun buildUidTable(docRoot: DocumentRootNode, map: MutableMap<String, TopLevelDeclaration> = mutableMapOf()): Map<String, TopLevelDeclaration> {
    docRoot.declarations.forEach { declaration ->
        when (declaration) {
            is ClassNode -> map[declaration.uid] = declaration
            is FunctionNode -> map[declaration.uid] = declaration
            is VariableNode -> map[declaration.uid] = declaration
            is DocumentRootNode -> buildUidTable(declaration, map)
            else -> Unit
        }
    }
    return map
}

fun introduceExportAnnotations(docRoot: DocumentRootNode, uidTable: Map<String, TopLevelDeclaration>): DocumentRootNode {

    val declarations = docRoot.declarations.map { declaration ->
        when (declaration) {
            is DocumentRootNode -> listOf(introduceExportAnnotations(declaration, uidTable))

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
                        is ClassNode -> {
                            entity.annotations.add(AnnotationNode("JsModule", listOf(docRoot.qualifierName)))
                            emptyList<TopLevelDeclaration>()
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

fun DocumentRootNode.introduceExports(): DocumentRootNode {
    val uidTable = buildUidTable(this)
    val docRoot =  introduceExportAnnotations(this, uidTable)

    return docRoot
}