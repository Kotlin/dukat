package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.HeritageSymbolNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.ImportNode
import org.jetbrains.dukat.ast.model.nodes.NameNode
import org.jetbrains.dukat.ast.model.nodes.PropertyAccessNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.ast.model.nodes.translate
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.IdentifierDeclaration
import org.jetbrains.dukat.tsmodel.QualifiedLeftDeclaration
import org.jetbrains.dukat.tsmodel.QualifiedNamedDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetrbains.dukat.nodeLowering.NodeWithOwnerTypeLowering

private fun NodeOwner<*>.getModule(): DocumentRootNode {
    return (getOwners().first { (it is NodeOwner<*>) && (it.node is DocumentRootNode) } as NodeOwner<DocumentRootNode>).node
}

private fun NodeOwner<*>.findModuleWithImport(value: String): ImportNode? {
    return getOwners().mapNotNull { docRootOwner ->
        if ((docRootOwner is NodeOwner<*>) && (docRootOwner.node is DocumentRootNode)) {
            (docRootOwner.node as DocumentRootNode).imports.get(value)
        } else {
            null
        }

    }.firstOrNull()
}

private class UidData() {
    private val myUidData = mutableMapOf<String, List<DocumentRootNode>>()

    fun register(uid: String, documentRootNodes: List<DocumentRootNode>) {
        myUidData[uid] = documentRootNodes
    }

    fun resolve(uid: String?): List<DocumentRootNode>? {
        return myUidData.get(uid)
    }
}

private class LowerQualifiedDeclarations(private val uidData: UidData) : NodeWithOwnerTypeLowering {

    private fun resolve(value: String, owner: NodeOwner<*>): NameNode {
        val importNode = owner.findModuleWithImport(value)

        return if (importNode == null) {
            IdentifierNode(value)
        } else {
            val importedDocumentNodes = uidData.resolve(importNode.uid)
            if (importedDocumentNodes == null) {
                importNode.referenceName
            } else {
                val resolvedImport = importedDocumentNodes.firstOrNull { importedDocumentNode ->
                    importedDocumentNode.packageName == importNode.referenceName.translate()
                }

                if (resolvedImport == null) {
                    importNode.referenceName
                } else {
                    resolvedImport.qualifiedNode as NameNode
                }
            }
        }
    }


    // TODO: This kind of declarations are not supposed to survive up to this point at all
    private fun resolve(value: QualifiedLeftDeclaration, owner: NodeOwner<*>): NameNode {
        return when (value) {
            is IdentifierDeclaration -> {
                resolve(value.value, owner)
            }
            is QualifiedNamedDeclaration -> {
                QualifiedNode(
                        left = resolve(value.left, owner),
                        right = IdentifierNode(value.right.value),
                        nullable = value.nullable
                )
            }
            else -> raiseConcern("unknown QualifiedLeftDeclaration subtype") { IdentifierNode("INVALID_NODE") }
        }
    }

    private fun resolve(value: NameNode, owner: NodeOwner<*>): NameNode {
        return when (value) {
            is IdentifierNode -> {
                resolve(value.value, owner)
            }
            is QualifiedNode -> {
                QualifiedNode(
                        left = resolve(value.left, owner),
                        right = IdentifierNode(value.right.value),
                        nullable = value.nullable
                )
            }
            else -> raiseConcern("unknown QualifiedLeftDeclaration subtype") { IdentifierNode("INVALID_NODE") }
        }
    }


    private fun resolveExression(heritageSymbol: HeritageSymbolNode, ownerModule: DocumentRootNode): HeritageSymbolNode {
        return when (heritageSymbol) {
            is IdentifierNode -> {
                val reference = ownerModule.imports.get(heritageSymbol.value)
                when (reference?.referenceName) {
                    is IdentifierNode -> reference.referenceName
                    else -> heritageSymbol
                }
            }
            is PropertyAccessNode -> {
                return heritageSymbol.copy(expression = resolveExression(heritageSymbol.expression, ownerModule))
            }
            else -> heritageSymbol
        }
    }

    override fun lowerParameterValue(owner: NodeOwner<ParameterValueDeclaration>): ParameterValueDeclaration {
        val declaration = owner.node
        return when (declaration) {
            is TypeValueNode -> declaration.copy(value = resolve(declaration.value, owner))
            is QualifiedNamedDeclaration -> QualifiedNode(
                    left = resolve(declaration.left, owner),
                    right = IdentifierNode(declaration.right.value),
                    nullable = declaration.nullable
            )
            else -> super.lowerParameterValue(owner)
        }
    }

    override fun lowerHeritageNode(owner: NodeOwner<HeritageNode>): HeritageNode {
        val heritageClause = owner.node
        return heritageClause.copy(name = resolveExression(heritageClause.name, owner.getModule()))
    }

}


private fun DocumentRootNode.collectUidData(uidData: UidData): DocumentRootNode {
    val head = if (owner == null) emptyList() else listOf(owner!!)
    uidData.register(uid, head + declarations.mapNotNull { declaration ->
        if (declaration is DocumentRootNode) {
            declaration.collectUidData(uidData)
        } else null
    })

    return this
}

private fun DocumentRootNode.introduceQualifiedNode(): DocumentRootNode {
    val uidData = org.jetbrains.dukat.nodeIntroduction.UidData()
    collectUidData(uidData)

    return org.jetbrains.dukat.nodeIntroduction.LowerQualifiedDeclarations(uidData).lowerRoot(this, NodeOwner(this, null))
}

fun SourceSetNode.introduceQualifiedNode() = transform { it.introduceQualifiedNode() }