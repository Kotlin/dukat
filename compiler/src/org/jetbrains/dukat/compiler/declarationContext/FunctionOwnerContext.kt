package org.jetbrains.dukat.compiler.declarationContext

import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ownerContext.OwnerContext

class FunctionOwnerContext(
        val node: FunctionNode,
        override val owner: OwnerContext
) : TypeOwnerContext