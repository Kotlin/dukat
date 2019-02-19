package org.jetbrains.dukat.compiler.declarationContext

import org.jetbrains.dukat.ast.model.nodes.PropertyNode


class PropertyOwnerContext(
        val node: PropertyNode,
        override val owner: DeclarationContext
) : TypeOwnerContext
