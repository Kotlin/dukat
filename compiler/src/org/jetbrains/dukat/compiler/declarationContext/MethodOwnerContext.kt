package org.jetbrains.dukat.compiler.declarationContext

import org.jetbrains.dukat.ast.model.nodes.MethodNode

class MethodOwnerContext(
        val node: MethodNode,
        override val owner: DeclarationContext
) : TypeOwnerContext
