package org.jetbrains.dukat.compiler.declarationContext

import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ownerContext.OwnerContext


class PropertyOwnerContext(
        val node: PropertyNode,
        override val owner: OwnerContext
) : TypeOwnerContext
