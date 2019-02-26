package org.jetbrains.dukat.compiler.declarationContext

import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ownerContext.OwnerContext

class MethodOwnerContext(
        val node: MethodNode,
        override val owner: OwnerContext
) : TypeOwnerContext
