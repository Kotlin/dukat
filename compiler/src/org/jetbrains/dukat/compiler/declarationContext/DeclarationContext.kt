package org.jetbrains.dukat.compiler.declarationContext

interface DeclarationContext {
    val owner: DeclarationContext

    private fun getOwners()  = generateSequence(this) {
        if (it.owner == ROOT_DECLARATION_CONTEXT) {
            null
        } else {
            it.owner
        }
    }

    fun getModuleOwnerContext() : ModuleModelOwnerContext? {
        return getOwners().firstOrNull { it is ModuleModelOwnerContext } as ModuleModelOwnerContext?
    }
}