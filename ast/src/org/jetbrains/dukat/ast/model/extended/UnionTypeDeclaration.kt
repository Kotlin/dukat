package org.jetbrains.dukat.ast.model.extended

import org.jetbrains.dukat.ast.model.ParameterValue

data class UnionTypeDeclaration(
    val params: List<ParameterValue>,

    override var nullable: Boolean = false,
    override var vararg: Boolean = false,
    override var meta: ParameterValue? = null
) : ParameterValue