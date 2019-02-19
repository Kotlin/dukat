package org.jetbrains.dukat.compiler.declarationContext

import org.jetbrains.dukat.ast.model.model.ClassModel

class ClassModelOwnerContext(
        val node: ClassModel,
        override val owner: DeclarationContext
) : TypeOwnerContext, ClassLikeOwnerContext