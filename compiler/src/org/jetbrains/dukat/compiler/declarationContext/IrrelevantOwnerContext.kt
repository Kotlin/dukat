package org.jetbrains.dukat.compiler.declarationContext

import org.jetbrains.dukat.ownerContext.OwnerContext

class IrrelevantOwnerContext(
        override val owner: OwnerContext? = null
) : TypeOwnerContext