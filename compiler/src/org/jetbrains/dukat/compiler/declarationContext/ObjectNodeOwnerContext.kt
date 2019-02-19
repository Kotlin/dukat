package org.jetbrains.dukat.compiler.declarationContext

import org.jetbrains.dukat.ast.model.nodes.ObjectNode

class ObjectNodeOwnerContext(
        val node: ObjectNode,
        override val owner: DeclarationContext
) : TypeOwnerContext, ClassLikeOwnerContext