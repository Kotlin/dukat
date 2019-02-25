package org.jetbrains.dukat.compiler.declarationContext

import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedLeftNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.ast.model.nodes.appendLeft

interface DeclarationContext {
    val owner: DeclarationContext

    private fun getOwners() = generateSequence(this) {
        if (it.owner == ROOT_DECLARATION_CONTEXT) {
            null
        } else {
            it.owner
        }
    }

    private fun getModuleOwners(): Sequence<ModuleModelOwnerContext> {
        return getOwners()
                .filterIsInstance(ModuleModelOwnerContext::class.java)
    }

    fun getQualifiedName(): QualifiedLeftNode {
        val moduleOwners = getModuleOwners().toList()

        return if (moduleOwners.isEmpty()) {
            QualifiedNode(IdentifierNode("ROOT"), IdentifierNode("ROOT"))
        } else if (moduleOwners.size == 1) {
            return IdentifierNode(moduleOwners.get(0).node.shortName)
        } else {
            getModuleOwners()
                    .drop(1)
                    .fold(IdentifierNode(moduleOwners.first().node.shortName) as QualifiedLeftNode) { acc, a -> IdentifierNode(a.node.shortName).appendLeft(acc) } as QualifiedNode
        }



    }

    fun getModuleOwnerContext(): ModuleModelOwnerContext? {
        return getOwners().firstOrNull { it is ModuleModelOwnerContext } as ModuleModelOwnerContext?
    }
}