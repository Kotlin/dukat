package org.jetbrains.dukat.compiler.declarationContext

import org.jetbrains.dukat.ast.model.model.InterfaceModel
import org.jetbrains.dukat.ownerContext.OwnerContext

class InterfaceModelOwnerContext(
        val node: InterfaceModel,
        override val owner: OwnerContext
) : TypeOwnerContext, ClassLikeOwnerContext