package org.jetbrains.dukat.compiler.lowerings.merge

import org.jetbrains.dukat.ast.model.model.ClassModel
import org.jetbrains.dukat.ast.model.model.InterfaceModel
import org.jetbrains.dukat.ast.model.model.ModuleModel
import org.jetbrains.dukat.compiler.declarationContext.ModuleModelOwnerContext

private data class DeclarationKey(val classValue: String)

class DeclarationResolver {

    private val myDeclarations: MutableMap<DeclarationKey, ModuleModelOwnerContext> = mutableMapOf()

    fun process(documentRoot: ModuleModel, moduleContext: ModuleModelOwnerContext = ModuleModelOwnerContext(documentRoot)) {
        documentRoot.declarations.forEach { declaration ->
            if (declaration is ClassModel) {
                myDeclarations.put(DeclarationKey(declaration.name), moduleContext)
            } else if (declaration is InterfaceModel) {
                myDeclarations.put(DeclarationKey(declaration.name), moduleContext)
            }
        }

        documentRoot.sumbodules.forEach { submodule -> process(submodule, ModuleModelOwnerContext(submodule, moduleContext))}
    }

    fun resolve(name: String): ModuleModelOwnerContext? {
        return myDeclarations.get(DeclarationKey(name))
    }
}