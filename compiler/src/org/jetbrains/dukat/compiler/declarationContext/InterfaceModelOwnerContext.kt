package org.jetbrains.dukat.compiler.declarationContext

import org.jetbrains.dukat.ast.model.model.InterfaceModel

class InterfaceModelOwnerContext(
        val node: InterfaceModel,
        override val owner: DeclarationContext
) : TypeOwnerContext, ClassLikeOwnerContext