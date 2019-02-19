package org.jetbrains.dukat.compiler.declarationContext

import org.jetbrains.dukat.ast.model.model.ModuleModel

class ModuleModelOwnerContext(
        val node: ModuleModel,
        override val owner: DeclarationContext = ROOT_DECLARATION_CONTEXT
) : DeclarationContext