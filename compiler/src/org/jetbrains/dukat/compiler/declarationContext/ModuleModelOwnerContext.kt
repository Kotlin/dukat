package org.jetbrains.dukat.compiler.declarationContext

import org.jetbrains.dukat.ast.model.model.ModuleModel
import org.jetbrains.dukat.ownerContext.OwnerContext

class ModuleModelOwnerContext(
        val node: ModuleModel,
        override val owner: OwnerContext?
) : OwnerContext