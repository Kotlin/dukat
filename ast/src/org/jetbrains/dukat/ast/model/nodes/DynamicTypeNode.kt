package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration

data class DynamicTypeNode(
    val description: String?,

    override var nullable: Boolean = false,
    override var meta: ParameterValueDeclaration? = null
) : ParameterValueDeclaration