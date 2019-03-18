package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.HeritageSymbolNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.ImportNode
import org.jetbrains.dukat.ast.model.nodes.PropertyAccessNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedLeftNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.ast.model.nodes.translate
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.IdentifierDeclaration
import org.jetbrains.dukat.tsmodel.QualifiedLeftDeclaration
import org.jetbrains.dukat.tsmodel.QualifiedNamedDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

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

private class LowerQualifiedDeclarations(private val uidData: UidData) : NodeTypeLowering {

    private fun resolve(value: QualifiedLeftDeclaration, owner: NodeOwner<*>): QualifiedLeftNode {

        return when (value) {
            is IdentifierDeclaration -> {
                val importNode = owner.findModuleWithImport(value.value)

                if (importNode == null) {
                    IdentifierNode(value.value)
                } else {
                    val importedDocumentNodes = uidData.resolve(importNode.uid)
                    if (importedDocumentNodes == null) {
                        importNode.referenceName as QualifiedLeftNode
                    } else {
                        val resolvedImport = importedDocumentNodes.firstOrNull { importedDocumentNode ->
                            importedDocumentNode.packageName == importNode.referenceName.translate()
                        }

                        if (resolvedImport == null) {
                            importNode.referenceName as QualifiedLeftNode
                        } else {
                            resolvedImport.qualifiedNode as QualifiedLeftNode
                        }
                    }
                }
            }
            is QualifiedNamedDeclaration -> {
                QualifiedNode(resolve(value.left, owner), IdentifierNode(value.right.value))
            }
            else -> throw Exception("unknown QualifiedLeftDeclaration subtype")
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
            is QualifiedNamedDeclaration -> QualifiedNode(resolve(declaration.left, owner), IdentifierNode(declaration.right.value))
            else -> declaration
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

fun DocumentRootNode.introduceQualifiedNode(): DocumentRootNode {
    val uidData = UidData()
    collectUidData(uidData)

    return LowerQualifiedDeclarations(uidData).lowerRoot(this, NodeOwner(this, null))
}

fun SourceSetNode.introduceQualifiedNode() = transform { it.introduceQualifiedNode() }