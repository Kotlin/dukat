package org.jetbrains.dukat.compiler.declarationContext

import org.jetbrains.dukat.ownerContext.OwnerContext

data class TypeContext(
        override val owner: OwnerContext
) : OwnerContext