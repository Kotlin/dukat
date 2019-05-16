package org.jetbrains.dukat.compiler.lowerings.merge

import org.jetbrains.dukat.ast.model.nodes.processing.shiftRight
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.ownerContext.NodeOwner

private data class DeclarationKey(
        val classValue: String,
        val qualifiedPath: NameEntity
)

class DeclarationResolver {
    private val myDeclarations: MutableMap<DeclarationKey, NodeOwner<ModuleModel>> = mutableMapOf()

    fun process(documentRoot: ModuleModel, moduleContext: NodeOwner<ModuleModel>) {

        documentRoot.declarations.forEach { declaration ->
            if (declaration is ClassModel) {
                register(declaration.name, moduleContext)
            } else if (declaration is InterfaceModel) {
                register(declaration.name, moduleContext)
            }
        }

        documentRoot.sumbodules.forEach { submodule ->
            process(submodule, NodeOwner(submodule, moduleContext))
        }
    }

    private fun register(name: String, moduleContext: NodeOwner<ModuleModel>) {
        myDeclarations[DeclarationKey(name, moduleContext.getQualifiedName())] = moduleContext
    }

    fun resolve(name: String, path: NameEntity?): NodeOwner<ModuleModel>? {
        if (path == null) {
            return null
        }

        var qualifiedPath: NameEntity = path

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

    fun resolveStrict(name: String, qualifiedPath: NameEntity?): NodeOwner<ModuleModel>? {
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