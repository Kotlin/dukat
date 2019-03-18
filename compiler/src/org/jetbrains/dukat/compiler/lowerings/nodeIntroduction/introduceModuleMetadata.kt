package org.jetbrains.dukat.compiler.lowerings.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.NameNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.appendRight
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.ownerContext.NodeOwner


//TODO: this should be done somewhere near escapeIdentificators (at least code should be reused)
private fun escapePackageName(name: String): String {
    return name
            .replace("/".toRegex(), ".")
            .replace("-".toRegex(), "_")
            .replace("^_$".toRegex(), "`_`")
            .replace("^class$".toRegex(), "`class`")
            .replace("^var$".toRegex(), "`var`")
            .replace("^val$".toRegex(), "`val`")
            .replace("^interface$".toRegex(), "`interface`")
}

private fun unquote(name: String): String {
    return name.replace("(?:^\"|\')|(?:\"|\'$)".toRegex(), "")
}

fun DocumentRootNode.introduceModuleMetadata(nodeOwner: NodeOwner<DocumentRootNode>): DocumentRootNode {

    val parentDocRoots =
            nodeOwner.getOwners().asIterable().reversed().toMutableList() as MutableList<NodeOwner<DocumentRootNode>>

    val rootOwner = parentDocRoots.removeAt(0)
    val qualifiers = parentDocRoots.map { unquote(it.node.packageName) }

    val packageNameResolved = (listOf(resourceName) + qualifiers).joinToString(".") { escapePackageName(it) }
    fullPackageName = packageNameResolved

    isQualifier = (packageName == unquote(packageName))

    showQualifierAnnotation = nodeOwner != rootOwner

    val isExternalDefinition = definitionsInfo.any { definition ->
        definition.fileName != fileName
    }
    if (isExternalDefinition && isQualifier) {
        showQualifierAnnotation = false
    }

    qualifiedNode = if (qualifiers.isEmpty()) {
        null
    } else {
        qualifiers.map { IdentifierNode(it) }
                .reduce<NameNode, NameNode> { acc, identifierNode ->
                    identifierNode.appendRight(acc)
                }
    }

    declarations.forEach { declaration ->
        if (declaration is DocumentRootNode) {
            declaration.introduceModuleMetadata(nodeOwner.wrap(declaration))
        }
    }

    return this
}

fun SourceSetNode.introduceModuleMetadata() = transform { it.introduceModuleMetadata(NodeOwner(it, null)) }
