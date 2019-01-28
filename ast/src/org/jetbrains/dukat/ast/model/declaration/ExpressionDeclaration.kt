package org.jetbrains.dukat.ast.model.declaration

import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration

data class ExpressionDeclaration(
        val kind: TypeDeclaration,
        val meta: String?
) : Declaration