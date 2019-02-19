package org.jetbrains.dukat.compiler.declarationContext

data class TypeContext(
        override val owner: DeclarationContext
) : DeclarationContext