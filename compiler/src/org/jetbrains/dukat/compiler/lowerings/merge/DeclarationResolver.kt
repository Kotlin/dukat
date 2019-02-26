package org.jetbrains.dukat.compiler.lowerings.merge

import org.jetbrains.dukat.ast.model.model.ClassModel
import org.jetbrains.dukat.ast.model.model.InterfaceModel
import org.jetbrains.dukat.ast.model.model.ModuleModel
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedLeftNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.ast.model.nodes.appendLeft
import org.jetbrains.dukat.ast.model.nodes.shiftRight
import org.jetbrains.dukat.compiler.declarationContext.ModuleModelOwnerContext
import org.jetbrains.dukat.ownerContext.OwnerContext

private data class DeclarationKey(
    val classValue: String,
    val qualifiedPath: QualifiedLeftNode
)

fun OwnerContext.getQualifiedName(): QualifiedLeftNode {
    val moduleOwners = getOwners()
            .filterIsInstance(ModuleModelOwnerContext::class.java)
            .toList()

    return if (moduleOwners.isEmpty()) {
        QualifiedNode(IdentifierNode("ROOT"), IdentifierNode("ROOT"))
    } else if (moduleOwners.size == 1) {
        return IdentifierNode(moduleOwners.get(0).node.shortName)
    } else {
        moduleOwners
                .drop(1)
                .fold(IdentifierNode(moduleOwners.first().node.shortName) as QualifiedLeftNode) { acc, a -> IdentifierNode(a.node.shortName).appendLeft(acc) } as QualifiedNode
    }
}


class DeclarationResolver {
    private val myDeclarations: MutableMap<DeclarationKey, ModuleModelOwnerContext> = mutableMapOf()

    fun process(documentRoot: ModuleModel, moduleContext: ModuleModelOwnerContext) {

        documentRoot.declarations.forEach { declaration ->
            if (declaration is ClassModel) {
                register(declaration.name, moduleContext)
            } else if (declaration is InterfaceModel) {
                register(declaration.name, moduleContext)
            }
        }

        documentRoot.sumbodules.forEach { submodule ->
            process(submodule, ModuleModelOwnerContext(submodule, moduleContext))
        }
    }

    private fun register(name: String, moduleContext: ModuleModelOwnerContext) {
        myDeclarations.put(DeclarationKey(name, moduleContext.getQualifiedName()), moduleContext)
    }

    fun resolve(name: String, path: QualifiedLeftNode?): ModuleModelOwnerContext? {
        if (path == null) {
            return null
        }

        var qualifiedPath: QualifiedLeftNode = path

        while (true) {
            val declarationKey = DeclarationKey(name, qualifiedPath)

            if (myDeclarations.containsKey(declarationKey)) {
                return myDeclarations.get(declarationKey)
            }

            val shifted = qualifiedPath.shiftRight()
            if (shifted != null) {
                qualifiedPath = shifted
            } else {
                return null
            }
        }

    }

    fun resolveStrict(name: String, qualifiedPath: QualifiedLeftNode?): ModuleModelOwnerContext? {
        if (qualifiedPath == null) {
            return null
        }

        val declarationKey = DeclarationKey(name, qualifiedPath)

        if (myDeclarations.containsKey(declarationKey)) {
            return myDeclarations.get(declarationKey)
        }

        return null
    }

}