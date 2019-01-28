package org.jetbrains.dukat.ast.model.declaration

import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration

data class ParameterDeclaration(
        val name: String,
        val type: ParameterValueDeclaration,
        val initializer: ExpressionDeclaration?,

        val vararg: Boolean
) : Declaration