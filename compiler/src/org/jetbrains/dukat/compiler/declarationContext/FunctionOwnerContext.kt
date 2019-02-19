package org.jetbrains.dukat.compiler.declarationContext

import org.jetbrains.dukat.ast.model.nodes.FunctionNode

class FunctionOwnerContext(
        val node: FunctionNode,
        override val owner: DeclarationContext
) : TypeOwnerContext