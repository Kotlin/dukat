package org.jetbrains.dukat.ast.model.extended

import org.jetbrains.dukat.ast.model.MemberDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue

data class ObjectLiteral(
        val members: List<MemberDeclaration>,
        override var nullable: Boolean = false,
        override var vararg: Boolean = false,
        override var meta: ParameterValue? = null
) : ParameterValue, MemberDeclaration