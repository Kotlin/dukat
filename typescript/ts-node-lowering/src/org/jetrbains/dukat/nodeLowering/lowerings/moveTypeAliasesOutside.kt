package org.jetrbains.dukat.nodeLowering.lowerings

import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.SourceFileNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.visitors.visitTopLevelNode
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.astCommon.rightMost

private fun DocumentRootNode.isJsModule(): Boolean {
    return jsModule != null || jsQualifier != null
}

private fun TopLevelEntity.needToBeMoved(owner: DocumentRootNode): Boolean {
    return (this is TypeAliasNode) && (owner.isJsModule())
}

private fun DocumentRootNode.transformTopLevelNode(visit: (TopLevelEntity, DocumentRootNode) -> List<TopLevelEntity>?): DocumentRootNode {
    val declarationsTransformed = declarations.mapNotNull {
        when (it) {
            is DocumentRootNode -> listOf(it.transformTopLevelNode(visit))
            else -> visit(it, this)
        }
    }.flatten()

    return copy(declarations = declarationsTransformed)
}

private fun SourceSetNode.transformTopLevelNode(visit: (TopLevelEntity, DocumentRootNode) -> List<TopLevelEntity>?): SourceSetNode {
    return copy(sources = sources.map { source -> source.copy(root = source.root.transformTopLevelNode(visit)) })
}

private typealias AliasBuckets = Map<AliasInfo, List<TypeAliasNode>>

private data class AliasInfo(val name: NameEntity, val sourceFile: String)

private fun SourceSetNode.findTypeAliases(): AliasBuckets {
    val aliasBuckets = mutableMapOf<AliasInfo, MutableList<TypeAliasNode>>()

    visitTopLevelNode { node, ownerRoot, fileNode ->
        if (node.needToBeMoved(ownerRoot)) {
            aliasBuckets
                    .getOrPut(AliasInfo(ownerRoot.qualifiedPackageName, fileNode.fileName)) { mutableListOf() }
                    .add((node as TypeAliasNode).copy())
        }
    }

    return aliasBuckets
}


fun SourceSetNode.moveTypeAliasesOutside(): SourceSetNode {

    val buckets = findTypeAliases()

    val moduleWithoutAliases = transformTopLevelNode { node, owner ->
        if (node.needToBeMoved(owner)) {
            null
        } else {
            listOf(node)
        }
    }

    return if (buckets.isNotEmpty()) {
        val newSources = buckets.map { (aliasInfo, aliases) ->
            val packageName = aliasInfo.name
            SourceFileNode(
                    fileName = aliasInfo.sourceFile,
                    root = DocumentRootNode(
                            moduleName = null,
                            qualifiedPackageName = packageName,
                            packageName = packageName.rightMost(),
                            declarations = aliases,
                            imports = emptyMap(),
                            definitionsInfo = emptyList(),
                            external = false,
                            jsModule = null,
                            jsQualifier = null,
                            uid = ""
                    ),
                    referencedFiles = emptyList()
            )
        }
        moduleWithoutAliases.copy(sources = newSources + moduleWithoutAliases.sources)
    } else {
        moduleWithoutAliases
    }

}
