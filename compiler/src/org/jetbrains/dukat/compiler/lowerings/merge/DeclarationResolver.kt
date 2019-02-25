package org.jetbrains.dukat.compiler.lowerings.merge

import org.jetbrains.dukat.ast.model.model.ClassModel
import org.jetbrains.dukat.ast.model.model.InterfaceModel
import org.jetbrains.dukat.ast.model.model.ModuleModel
import org.jetbrains.dukat.ast.model.nodes.QualifiedLeftNode
import org.jetbrains.dukat.ast.model.nodes.shiftRight
import org.jetbrains.dukat.compiler.declarationContext.ModuleModelOwnerContext

private data class DeclarationKey(
    val classValue: String,
    val qualifiedPath: QualifiedLeftNode
)

class DeclarationResolver {
    private val myDeclarations: MutableMap<DeclarationKey, ModuleModelOwnerContext> = mutableMapOf()

    fun process(documentRoot: ModuleModel, moduleContext: ModuleModelOwnerContext = ModuleModelOwnerContext(documentRoot)) {

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