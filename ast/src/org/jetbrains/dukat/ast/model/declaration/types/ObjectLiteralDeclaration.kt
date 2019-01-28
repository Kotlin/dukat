package org.jetbrains.dukat.ast.model.declaration.types

import org.jetbrains.dukat.ast.model.declaration.MemberDeclaration

data class ObjectLiteralDeclaration(
        val members: List<MemberDeclaration>,
        override var nullable: Boolean = false,
        override var vararg: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : ParameterValueDeclaration, MemberDeclaration