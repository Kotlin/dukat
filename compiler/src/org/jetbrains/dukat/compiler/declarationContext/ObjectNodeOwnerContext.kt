package org.jetbrains.dukat.compiler.declarationContext

import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ownerContext.OwnerContext

class ObjectNodeOwnerContext(
        val node: ObjectNode,
        override val owner: OwnerContext?
) : TypeOwnerContext, ClassLikeOwnerContext