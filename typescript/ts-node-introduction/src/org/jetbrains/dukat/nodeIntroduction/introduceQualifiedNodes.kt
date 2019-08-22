package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.ImportNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetrbains.dukat.nodeLowering.NodeWithOwnerTypeLowering

@Suppress("UNCHECKED_CAST")
private fun NodeOwner<*>.getModule(): DocumentRootNode {
    return (getOwners().first { (it is NodeOwner<*>) && (it.node is DocumentRootNode) } as NodeOwner<DocumentRootNode>).node
}

private fun NodeOwner<*>.findModuleWithImport(value: String): ImportNode? {
    getOwners().forEach { docRootOwner ->
        if ((docRootOwner is NodeOwner<*>) && (docRootOwner.node is DocumentRootNode)) {
            (docRootOwner.node as DocumentRootNode).imports[value]?.let {
                return it
            }
        }
    }

    return null
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

    private fun resolve(value: String, owner: NodeOwner<*>): NameEntity {
        val importNode = owner.findModuleWithImport(value)

        return if (importNode == null) {
            IdentifierEntity(value)
        } else {
            val importedDocumentNodes = uidData.resolve(importNode.uid)
            if (importedDocumentNodes == null) {
                importNode.referenceName
            } else {
                val resolvedImport = importedDocumentNodes.firstOrNull { importedDocumentNode ->
                    importedDocumentNode.qualifiedPackageName.rightMost() == importNode.referenceName
                }

                if (resolvedImport == null) {
                    importNode.referenceName
                } else {
                    resolvedImport.qualifiedPackageName
                }
            }
        }
    }


    private fun resolve(value: NameEntity, owner: NodeOwner<*>): NameEntity {
        return when (value) {
            is IdentifierEntity -> {
                resolve(value.value, owner)
            }
            is QualifierEntity -> {
                QualifierEntity(
                        left = resolve(value.left, owner),
                        right = IdentifierEntity(value.right.value)
                )
            }
            else -> raiseConcern("unknown NameEntity subtype ${value::class.simpleName}") { value }
        }
    }

    private fun resolveExression(heritageSymbol: NameEntity, ownerModule: DocumentRootNode): NameEntity {
        return when (heritageSymbol) {
            is IdentifierEntity -> {
                val reference = ownerModule.imports.get(heritageSymbol.value)
                when (reference?.referenceName) {
                    is IdentifierEntity -> reference.referenceName
                    else -> heritageSymbol
                }
            }
            is QualifierEntity -> {
                return heritageSymbol.copy(left = resolveExression(heritageSymbol.left, ownerModule))
            }
            else -> heritageSymbol
        }
    }

    override fun lowerParameterValue(owner: NodeOwner<ParameterValueDeclaration>): ParameterValueDeclaration {
        return when (val declaration = owner.node) {
            is TypeValueNode -> declaration.copy(value = resolve(declaration.value, owner))
            else -> super.lowerParameterValue(owner)
        }
    }

    override fun lowerHeritageNode(owner: NodeOwner<HeritageNode>): HeritageNode {
        val heritageClause = owner.node
        return heritageClause.copy(name = resolveExression(heritageClause.name, owner.getModule()))
    }

}


private fun DocumentRootNode.collectUidData(uidData: UidData, anOwner: DocumentRootNode?): DocumentRootNode {
    val head = if (anOwner == null) emptyList() else listOf(anOwner)
    uidData.register(uid, head + declarations.mapNotNull { declaration ->
        if (declaration is DocumentRootNode) {
            declaration.collectUidData(uidData, this)
        } else null
    })

    return this
}

private fun DocumentRootNode.introduceQualifiedNode(): DocumentRootNode {
    val uidData = UidData()
    collectUidData(uidData, null)

    return LowerQualifiedDeclarations(uidData).lowerRoot(this, NodeOwner(this, null))
}

fun SourceSetNode.introduceQualifiedNode() = transform { it.introduceQualifiedNode() }