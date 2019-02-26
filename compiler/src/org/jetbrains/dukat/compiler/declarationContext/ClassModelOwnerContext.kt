package org.jetbrains.dukat.compiler.declarationContext

import org.jetbrains.dukat.ast.model.model.ClassModel
import org.jetbrains.dukat.ownerContext.OwnerContext

class ClassModelOwnerContext(
        val node: ClassModel,
        override val owner: OwnerContext
) : TypeOwnerContext, ClassLikeOwnerContext