package org.jetbrains.dukat.compiler.declarationContext

object ROOT_DECLARATION_CONTEXT : DeclarationContext {
    override val owner: DeclarationContext
        get() = ROOT_DECLARATION_CONTEXT
}